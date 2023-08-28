package me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity

data class AuthenticationTokenCache(
    val key: String,
    val value: AuthenticationCredentials,
    val ttl: Long,
)

data class AuthenticationCredentials(
    val principal: Principal,
    val credentials: Credentials,
)

data class Credentials(
    val accessToken: String,
    val refreshToken: String,
)

data class Principal(
    val identifier: Long,
)