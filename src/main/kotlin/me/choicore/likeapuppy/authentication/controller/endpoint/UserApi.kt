package me.choicore.likeapuppy.authentication.controller.endpoint

import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Identifier
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/users")
class UserApi {

    @GetMapping("/me")
    fun getMe(
        @AuthenticationPrincipal identifier: Identifier,
    ): ResponseEntity<*> {

        return ResponseEntity.ok(
            mapOf(
                "code" to "000",
                "message" to "SUCCEED",
                "data" to mapOf("identifier" to identifier)
            )
        )
    }
}