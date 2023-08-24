package me.choicore.demo.springsecurity.authentication.exception

import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ExceptionAdvisor {
    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(e: UnauthorizedException): ResponseEntity<Any> {
        val errorCode = getUnauthorizedErrorCode(e)

        return ResponseEntity(
            mapOf(
                "code" to mapOf(
                    "errorCode" to errorCode.code,
                    "errorMessage" to errorCode.errorMessage
                ), "message" to e.message
            ), UNAUTHORIZED
        )
    }
}

internal fun getUnauthorizedErrorCode(e: UnauthorizedException): UnauthorizedErrorCode {
    return when (e) {
        is UsernameNotFoundException -> UnauthorizedErrorCode.USERNAME_NOT_FOUND
        is InvalidPasswordException -> UnauthorizedErrorCode.INVALID_PASSWORD
        is ExceededLoginAttemptsException -> UnauthorizedErrorCode.EXCEEDED_LOGIN_ATTEMPTS
    }
}

internal enum class UnauthorizedErrorCode(val code: String, val errorMessage: String) {
    USERNAME_NOT_FOUND("0001", "INVALID_CREDENTIALS"),
    INVALID_PASSWORD("0002", "INVALID_CREDENTIALS"),
    EXCEEDED_LOGIN_ATTEMPTS("0003", "EXCEEDED_LOGIN_ATTEMPTS"),

}
