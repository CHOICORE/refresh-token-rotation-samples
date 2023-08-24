package me.choicore.demo.springsecurity.authentication.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.choicore.demo.springsecurity.authentication.exception.InvalidPasswordException
import org.springframework.http.HttpHeaders
import org.springframework.web.filter.OncePerRequestFilter


class JwtAuthenticationFilter(
    private val jwtAuthenticationTokenProvider: JwtAuthenticationTokenProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        with(header) {
            if (this == null || !this.startsWith(GrantType.BEARER.value, ignoreCase = true)) {
                filterChain.doFilter(request, response)
                return
            }
        }

        val token = parseAuthorizationHeader(header)

        val validateToken = jwtAuthenticationTokenProvider.validateToken(token)

        check(validateToken) {
            throw InvalidPasswordException()
        }
    }

    private fun parseAuthorizationHeader(header: String): String {
        header.startsWith(GrantType.BEARER.value).let {
            return header.substring(GrantType.BEARER.value.length)
        }
    }

    enum class GrantType(val value: String) {
        BEARER("Bearer ");
    }

}

