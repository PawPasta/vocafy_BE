package com.exe.vocafy_BE.handler


open class BaseException (
    val statusCode: Int,
    override val message: String,
): RuntimeException() {

    class BadRequestException(
        message: String = "Bad Request"
    ) : BaseException(400, message)

    class UnauthorizedException(
        message: String = "Unauthorized"
    ) : BaseException(401, message)

    class ForbiddenException(
        message: String = "Forbidden"
    ) : BaseException(403, message)

    class NotFoundException(
        message: String = "Not Found"
    ) : BaseException(404, message)

    class ConflictException(
        message: String = "Conflict"
    ): BaseException(409, message)

}