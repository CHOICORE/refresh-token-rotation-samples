package me.choicore.demo.springsecurity.authentication.exception

import org.springframework.security.core.AuthenticationException


sealed class UnauthorizedException(override val message: String, val errorCode: UnauthorizedErrorCode) : AuthenticationException(message) {
    // Invalid Credentials
    data object UsernameNotFound : UnauthorizedException(message = "아이디 또는 비밀번호가 잘못되었습니다.", errorCode = UnauthorizedErrorCode.USERNAME_NOT_FOUND)
    data object InvalidPassword : UnauthorizedException(message = "아이디 또는 비밀번호가 잘못되었습니다.", errorCode = UnauthorizedErrorCode.INVALID_PASSWORD)
    data object InvalidToken : UnauthorizedException(message = "유효하지 않은 토큰입니다.", errorCode = UnauthorizedErrorCode.INVALID_TOKEN)
    data object ExpiredToken : UnauthorizedException(message = "만료된 토큰입니다.", errorCode = UnauthorizedErrorCode.EXPIRED_TOKEN)

    // Inactive Account
    data object DormantAccount : UnauthorizedException(message = "휴면 계정입니다.", errorCode = UnauthorizedErrorCode.INACTIVE_ACCOUNT_EXCEPTION)
    data object LoginAttemptsExceeded : UnauthorizedException(message = "로그인 시도 횟수를 초과하였습니다.", errorCode = UnauthorizedErrorCode.LOGIN_ATTEMPTS_EXCEEDED)

    // Not handled By Application
    data object InternalError : UnauthorizedException(message = "알 수 없는 오류가 발생했습니다.", errorCode = UnauthorizedErrorCode.UNKNOWN)
}
