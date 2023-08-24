package me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity

data class AuthenticationTokenStore(
    val key: String,
    val value: AuthenticationCredentials,
)

data class AuthenticationCredentials(
    val identifier: Long,
    val credentials: Credentials,
)

data class Credentials(
    val accessToken: String,
    val refreshToken: String,
)
