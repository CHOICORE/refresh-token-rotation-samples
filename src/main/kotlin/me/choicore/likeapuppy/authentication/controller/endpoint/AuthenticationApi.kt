package me.choicore.likeapuppy.authentication.controller.endpoint

import jakarta.validation.Valid
import me.choicore.likeapuppy.authentication.controller.dto.request.RefreshTokenDto
import me.choicore.likeapuppy.authentication.service.AuthenticationProcessor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/auth")
class AuthenticationApi(
    private val authenticationProcessor: AuthenticationProcessor,
) {
    @PostMapping("/token")
    fun refreshToken(
        @RequestBody @Valid refreshTokenDto: RefreshTokenDto,
    ): ResponseEntity<*> {
        return ResponseEntity.ok(
            mapOf(
                "code" to 0,
                "message" to "SUCCEED",
                "data" to authenticationProcessor.refreshTokenRotation(issuedRefreshToken = refreshTokenDto.refreshToken)
            )
        )
    }
}