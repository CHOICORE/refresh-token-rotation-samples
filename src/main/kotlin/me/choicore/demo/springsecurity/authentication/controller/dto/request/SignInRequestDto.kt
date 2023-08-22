package me.choicore.demo.springsecurity.authentication.controller.dto.request

import jakarta.validation.constraints.NotBlank

data class SignInRequestDto(
    @field:NotBlank val identifier: String,
    @field:NotBlank val password: String,
)
