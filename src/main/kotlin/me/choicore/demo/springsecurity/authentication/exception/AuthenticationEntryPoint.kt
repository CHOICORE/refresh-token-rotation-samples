package me.choicore.demo.springsecurity.authentication.exception

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

class AuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        authenticationException: AuthenticationException
    ) {
        with(httpServletResponse) {
            if (authenticationException !is UnauthorizedException) setHeader(HttpHeaders.WWW_AUTHENTICATE, authenticationException.message)
            status = HttpStatus.UNAUTHORIZED.value()
            contentType = MediaType.APPLICATION_JSON_VALUE
            val errorMessage: Map<String, Any> = getUnauthorizedErrorMessage(authenticationException)
            val errorMessageAsString: String = objectMapper.writeValueAsString(errorMessage)
            writer.write(errorMessageAsString)
        }
    }
}