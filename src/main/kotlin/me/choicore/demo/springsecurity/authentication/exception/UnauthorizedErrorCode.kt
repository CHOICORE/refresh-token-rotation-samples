package me.choicore.demo.springsecurity.authentication.exception

import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException

internal enum class UnauthorizedErrorCode(val code: String, val errorMessage: String) {
    USERNAME_NOT_FOUND("0001", "INVALID_CREDENTIALS"),
    INVALID_PASSWORD("0002", "INVALID_CREDENTIALS"),
    EXCEEDED_LOGIN_ATTEMPTS("0003", "EXCEEDED_LOGIN_ATTEMPTS"),
    INVALID_TOKEN("0004", "INVALID_TOKEN"),
    UNKNOWN("9999", "UNKNOWN"),
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

internal fun getUnauthorizedErrorCode(e: AuthenticationException): Pair<UnauthorizedErrorCode, String> {
    return when (e) {
        is UsernameNotFoundException -> Pair(UnauthorizedErrorCode.USERNAME_NOT_FOUND, e.message ?: "")
        is InvalidPasswordException -> Pair(UnauthorizedErrorCode.INVALID_PASSWORD, e.message ?: "")
        is ExceededLoginAttemptsException -> Pair(UnauthorizedErrorCode.EXCEEDED_LOGIN_ATTEMPTS, e.message ?: "")
        is InvalidBearerTokenException -> Pair(UnauthorizedErrorCode.INVALID_TOKEN, "유효하지 않은 토큰입니다.")
        else -> Pair(UnauthorizedErrorCode.UNKNOWN, "알 수 없는 오류입니다.")
    }
}