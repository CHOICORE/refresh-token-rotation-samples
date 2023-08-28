package me.choicore.likeapuppy.authentication.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "authentication")
data class AuthenticationProperties(
    val loginAttemptsLimit: Int,
)