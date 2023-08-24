package me.choicore.demo.springsecurity.authentication.jwt

sealed class AuthorizationToken {
    data class Bearer(val token: String) : AuthorizationToken() {
        override fun toString(): String {
            return "Bearer $token"
        }
    }
}
