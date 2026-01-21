package com.exe.vocafy_BE.handler

/**
 * Base exception class for all application exceptions.
 * All custom exceptions should extend this class.
 */
open class BaseException(
    val statusCode: Int,
    override val message: String,
) : RuntimeException(message) {

    // ==================== 4xx Client Errors ====================

    /** 400 Bad Request - Invalid request syntax or parameters */
    class BadRequestException(
        message: String = "Bad Request"
    ) : BaseException(400, message)

    /** 401 Unauthorized - Missing or invalid authentication */
    class UnauthorizedException(
        message: String = "Unauthorized"
    ) : BaseException(401, message)

    /** 401 - Missing authentication token */
    class MissingTokenException(
        message: String = "Missing token"
    ) : BaseException(401, message)

    /** 401 - Invalid or expired authentication token */
    class InvalidTokenException(
        message: String = "Invalid token"
    ) : BaseException(401, message)

    /** 403 Forbidden - Authenticated but not authorized */
    class ForbiddenException(
        message: String = "Forbidden"
    ) : BaseException(403, message)

    /** 404 Not Found - Resource does not exist */
    class NotFoundException(
        message: String = "Not Found"
    ) : BaseException(404, message)

    /** 409 Conflict - Resource conflict (e.g., duplicate) */
    class ConflictException(
        message: String = "Conflict"
    ) : BaseException(409, message)

    /** 422 Unprocessable Entity - Validation error */
    class ValidationException(
        message: String = "Validation error"
    ) : BaseException(422, message)

    // ==================== 5xx Server Errors ====================

    /** 500 Internal Server Error - Unexpected server error */
    class InternalServerException(
        message: String = "Internal server error"
    ) : BaseException(500, message)

    /** 503 Service Unavailable - Service temporarily unavailable */
    class ServiceUnavailableException(
        message: String = "Service unavailable"
    ) : BaseException(503, message)
}