package me.choicore.demo.springsecurity.authentication.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service


@Service
class AuthenticationProcessor(
    private val authenticationManager: AuthenticationManager,
) {
    fun authenticate(identifier: String, password: String): Authentication {
        return UsernamePasswordAuthenticationToken(identifier, password).let {
            authenticationManager.authenticate(it)
        }
    }
}