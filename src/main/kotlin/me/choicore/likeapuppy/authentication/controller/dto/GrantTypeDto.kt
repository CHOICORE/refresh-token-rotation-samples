package me.choicore.likeapuppy.authentication.controller.dto

enum class GrantTypeDto(val value: String) {
    REFRESH_TOKEN("refresh_token"),
    AUTHORIZATION_CODE("authorization_code")
}