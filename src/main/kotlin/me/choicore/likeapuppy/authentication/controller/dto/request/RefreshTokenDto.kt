package me.choicore.likeapuppy.authentication.controller.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import me.choicore.likeapuppy.authentication.controller.dto.GrantTypeDto

data class RefreshTokenDto(
    @field:NotNull val grantType: GrantTypeDto,
    @field:NotBlank val clientId: String,
    val clientSecret: String? = null,
    @field:NotBlank val refreshToken: String,
)