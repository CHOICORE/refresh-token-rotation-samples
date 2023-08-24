package me.choicore.demo.springsecurity.authentication.exception

import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ExceptionAdvisor {
    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(e: UnauthorizedException): ResponseEntity<Any> {
        return ResponseEntity(getUnauthorizedErrorMessage(e), UNAUTHORIZED)
    }
}
