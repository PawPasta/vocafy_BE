package com.exe.vocafy_BE.handler

/**
 * Base exception class for all custom exceptions in the application.
 * All exceptions extend from this class to ensure consistent error handling.
 */
open class BaseException(
    val statusCode: Int,
    override val message: String,
) : RuntimeException(message) {

    // 400 - Bad Request
    class BadRequestException(message: String = "Bad Request") : BaseException(400, message)

    // 401 - Unauthorized
    class UnauthorizedException(message: String = "Unauthorized") : BaseException(401, message)
    class MissingTokenException(message: String = "Missing token") : BaseException(401, message)
    class InvalidTokenException(message: String = "Invalid token") : BaseException(401, message)

    // 403 - Forbidden
    class ForbiddenException(message: String = "Forbidden") : BaseException(403, message)

    // 404 - Not Found
    class NotFoundException(message: String = "Not Found") : BaseException(404, message)

    // 409 - Conflict
    class ConflictException(message: String = "Conflict") : BaseException(409, message)

    // 409 - Conflict (domain specific)
    class AlreadyPremiumException(message: String = "User already has premium") : BaseException(409, message)

    // 500 - Internal Server Error
    class InternalServerException(message: String = "Internal server error") : BaseException(500, message)
}