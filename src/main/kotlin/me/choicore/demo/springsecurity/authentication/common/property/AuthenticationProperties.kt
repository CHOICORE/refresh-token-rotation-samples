package me.choicore.demo.springsecurity.authentication.common.property

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "authentication")
data class AuthenticationProperties(
    val loginAttemptsLimit: Int,
)