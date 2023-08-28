package me.choicore.demo.springsecurity.authentication.exception

import org.springframework.security.core.AuthenticationException
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
    with(getUnauthorizedErrorCode(e)) {
        return mapOf(
            "code" to mapOf(
                "errorCode" to this.first.code,
                "errorMessage" to this.first.errorMessage,
            ),
            "message" to this.second,
        )
    }
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
