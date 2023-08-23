package me.choicore.demo.springsecurity.authentication.common.handler

import me.choicore.demo.springsecurity.authentication.exception.UnauthorizedException
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ExceptionAdvisor {
    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(e: UnauthorizedException): ResponseEntity<Any> {
        return ResponseEntity(mapOf("code" to -1, "message" to e.message), UNAUTHORIZED)
    }
}