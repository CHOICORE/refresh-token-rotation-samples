package me.choicore.likeapuppy.authentication.controller.endpoint

import jakarta.validation.Valid
import me.choicore.likeapuppy.authentication.controller.dto.request.SignInRequestDto
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Identifier
import me.choicore.likeapuppy.authentication.service.AuthenticationProcessor
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/accounts")
class AccountApi(
    private val authenticationProcessor: AuthenticationProcessor,
) {

    @PostMapping("/sign-in")
    fun signIn(@RequestBody @Valid signInRequestDto: SignInRequestDto): ResponseEntity<*> {
        return ResponseEntity.ok(
            mapOf(
                "code" to 0,
                "message" to "SUCCEED",
                "data" to authenticationProcessor.getAuthenticationToken(
                    identifier = signInRequestDto.identifier,
                    password = signInRequestDto.password
                )
            )
        )
    }

    @PostMapping("/sign-out")
    fun signOut(@AuthenticationPrincipal identifier: Identifier): ResponseEntity<*> {
        authenticationProcessor.signOut(identifier)
        return ResponseEntity.ok(mapOf("code" to 0, "message" to "SUCCEED"))
    }
}