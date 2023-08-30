package me.choicore.likeapuppy.authentication.controller.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class RefreshTokenDto(
    @JsonProperty("refresh_token")
    @field:NotBlank val refreshToken: String,
)