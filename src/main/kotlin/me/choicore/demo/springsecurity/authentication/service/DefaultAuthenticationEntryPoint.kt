package me.choicore.demo.springsecurity.authentication.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.choicore.demo.springsecurity.authentication.exception.getUnauthorizedErrorMessage
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

class DefaultAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        with(response) {
            status = HttpStatus.UNAUTHORIZED.value()
            contentType = MediaType.APPLICATION_JSON_VALUE
            val errorMessage: Map<String, Any> = getUnauthorizedErrorMessage(authException)
            val errorMessageAsString: String = objectMapper.writeValueAsString(errorMessage)
            writer.write(errorMessageAsString)
        }
    }
}