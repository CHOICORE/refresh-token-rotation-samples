package me.choicore.likeapuppy.authentication.exception

import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.JwtValidationException
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException

enum class UnauthorizedErrorCode(val code: String, val errorMessage: String) {
    USERNAME_NOT_FOUND("001", "INVALID_CREDENTIALS"),
    INVALID_PASSWORD("002", "INVALID_CREDENTIALS"),
    LOGIN_ATTEMPTS_EXCEEDED("003", "LOGIN_ATTEMPTS_EXCEEDED"),
    INACTIVE_ACCOUNT_EXCEPTION("004", "INACTIVE_ACCOUNT"),
    INVALID_TOKEN("005", "INVALID_TOKEN"),
    EXPIRED_TOKEN("006", "EXPIRED_TOKEN"),
    UNKNOWN("-999", "UNKNOWN"),
}

internal fun getUnauthorizedErrorMessage(e: AuthenticationException): Map<String, Any> {
    val unauthorizedException: UnauthorizedException = e.parseException()
    val unauthorizedErrorCode: UnauthorizedErrorCode = unauthorizedException.errorCode

    return mapOf(
        "code" to mapOf(
            "errorCode" to unauthorizedErrorCode.code,
            "errorMessage" to unauthorizedErrorCode.errorMessage,
        ),
        "message" to unauthorizedException.message,
    )
}

private fun AuthenticationException.parseException(): UnauthorizedException {
    return when (this) {
        is UnauthorizedException -> this
        is InvalidBearerTokenException -> {
            when (cause) {
                is JwtValidationException -> UnauthorizedException.ExpiredToken
                is BadJwtException -> UnauthorizedException.InvalidToken
                else -> UnauthorizedException.InternalError
            }
        }

        else -> UnauthorizedException.InternalError
    }
}
