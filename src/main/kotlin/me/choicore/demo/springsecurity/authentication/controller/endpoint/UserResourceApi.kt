package me.choicore.demo.springsecurity.authentication.controller.endpoint

import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.Principal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/users")
class UserResourceApi {
    @GetMapping("/me")
    fun getMe(
        @AuthenticationPrincipal principal: Principal
    ): ResponseEntity<*> {
        return ResponseEntity.ok(mapOf("code" to "000", "message" to "SUCCEED", "data" to mapOf("userId" to principal.identifier)))
    }
}