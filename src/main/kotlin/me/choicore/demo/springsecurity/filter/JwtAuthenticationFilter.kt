package me.choicore.demo.springsecurity.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.choicore.demo.springsecurity.authentication.AuthorizationToken
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {

    }

    private fun parseAuthorizationHeader(header: String?): AuthorizationToken? {
        val prefix = "Bearer "
        if (header == null || !header.startsWith(prefix)) {
            return null
        }

        return AuthorizationToken.Bearer(header.substring(prefix.length))
    }
}


