package me.choicore.likeapuppy.authentication.controller.endpoint

import jakarta.validation.Valid
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Principal
import me.choicore.likeapuppy.authentication.service.AuthenticationProcessor
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/authentications")
class AuthenticationApi(
    private val authenticationProcessor: AuthenticationProcessor,
) {
    @PostMapping("/token")
    fun refreshToken(
        @AuthenticationPrincipal principal: Principal,
        @RequestBody @Valid refreshToken: String,
    ): ResponseEntity<*> {


        return ResponseEntity.ok(
            mapOf(
                "code" to 0,
                "message" to "SUCCEED",
            )
        )
    }
}