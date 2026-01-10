package com.exe.vocafy_BE.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.context.annotation.Primary
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.filter.OncePerRequestFilter
import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import com.exe.vocafy_BE.repo.LoginSessionRepository
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(
    SecurityAuthProperties::class,
    GoogleOauth2Properties::class,
    SecurityJwtProperties::class,
)
class SecurityConfig(
    private val authProperties: SecurityAuthProperties,
    private val googleOauth2Properties: GoogleOauth2Properties,
    private val jwtProperties: SecurityJwtProperties,
    private val loginSessionRepository: LoginSessionRepository,
) {

    @Bean
    @Primary
    fun jwtDecoder(): JwtDecoder {
        val secret = jwtProperties.secret
        val keySpec = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        val decoder = NimbusJwtDecoder.withSecretKey(keySpec).macAlgorithm(MacAlgorithm.HS256).build()
        val validator = JwtValidators.createDefault()
        decoder.setJwtValidator(validator)
        return decoder
    }

    @Bean("googleJwtDecoder")
    fun googleJwtDecoder(): JwtDecoder {
        val decoder = NimbusJwtDecoder.withJwkSetUri(googleOauth2Properties.jwkSetUri).build()
        decoder.setJwtValidator(IssuerOnlyValidator(googleOauth2Properties.issuer))
        return decoder
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val secret = jwtProperties.secret
        val keySpec = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        return NimbusJwtEncoder(ImmutableSecret(keySpec))
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val whitelistMatchers = authProperties.whitelist.map { AntPathRequestMatcher(it) }

        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                if (whitelistMatchers.isNotEmpty()) {
                    auth.requestMatchers(*whitelistMatchers.toTypedArray()).permitAll()
                }
                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { }
                oauth2.authenticationEntryPoint(InvalidTokenEntryPoint())
            }
            .addFilterBefore(
                MissingTokenFilter(whitelistMatchers),
                BearerTokenAuthenticationFilter::class.java,
            )
            .addFilterAfter(
                AccessTokenSessionFilter(whitelistMatchers, loginSessionRepository),
                BearerTokenAuthenticationFilter::class.java,
            )

        return http.build()
    }
}

class IssuerOnlyValidator(
    private val issuer: String,
) : OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> {

    override fun validate(token: org.springframework.security.oauth2.jwt.Jwt): OAuth2TokenValidatorResult {
        if (issuer.isBlank()) {
            return OAuth2TokenValidatorResult.success()
        }
        val matchesIssuer = token.issuer?.toString() == issuer
        return if (matchesIssuer) {
            OAuth2TokenValidatorResult.success()
        } else {
            OAuth2TokenValidatorResult.failure(OAuth2Error("invalid_token", "invalid issuer", null))
        }
    }
}

class MissingTokenFilter(
    private val whitelistMatchers: List<RequestMatcher>,
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        if (request.method == "OPTIONS") {
            return true
        }
        return whitelistMatchers.any { it.matches(request) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader.isNullOrBlank()) {
            writeError(response, "missing token")
            return
        }
        if (!authHeader.startsWith("Bearer ")) {
            writeError(response, "invalid token")
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun writeError(response: HttpServletResponse, message: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.writer.write("""{"success":false,"message":"$message","result":null}""")
    }
}

class AccessTokenSessionFilter(
    private val whitelistMatchers: List<RequestMatcher>,
    private val loginSessionRepository: LoginSessionRepository,
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        if (request.method == "OPTIONS") {
            return true
        }
        return whitelistMatchers.any { it.matches(request) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            writeError(response, "invalid token")
            return
        }
        val token = authHeader.removePrefix("Bearer ").trim()
        val session = loginSessionRepository.findByAccessTokenAndExpiredFalse(token)
        if (session == null) {
            writeError(response, "invalid token")
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun writeError(response: HttpServletResponse, message: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.writer.write("""{"success":false,"message":"$message","result":null}""")
    }
}

class InvalidTokenEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: org.springframework.security.core.AuthenticationException,
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.writer.write("""{"success":false,"message":"invalid token","result":null}""")
    }
}
