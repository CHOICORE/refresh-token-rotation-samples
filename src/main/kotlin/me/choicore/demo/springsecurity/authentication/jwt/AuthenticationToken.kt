package me.choicore.demo.springsecurity.authentication.jwt

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthenticationToken(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("expires_in")
    val expiresIn: Long,
    @JsonProperty("refresh_token")
    val refreshToken: String,
) {
    companion object {
        fun bearerToken(accessToken: String, expiresIn: Long, refreshToken: String): AuthenticationToken {
            return AuthenticationToken(
                accessToken = accessToken,
                tokenType = GrantType.BEARER.value,
                expiresIn = expiresIn,
                refreshToken = refreshToken,
            )
        }
    }
}

enum class GrantType(val value: String) {
    BEARER("Bearer");
}



