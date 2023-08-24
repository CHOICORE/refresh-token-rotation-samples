package me.choicore.demo.springsecurity.authentication.controller.endpoint

import jakarta.validation.Valid
import me.choicore.demo.springsecurity.authentication.controller.dto.request.SignInRequestDto
import me.choicore.demo.springsecurity.authentication.service.AuthenticationProcessor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/account")
class AuthenticationApi(
    private val authenticationProcessor: AuthenticationProcessor,
) {

    @PostMapping("/sign-in")
    fun signIn(@RequestBody @Valid signInRequestDto: SignInRequestDto): ResponseEntity<*> {
        return ResponseEntity.ok(
            mapOf(
                "code" to 0,
                "message" to "SUCCEED",
                "data" to authenticationProcessor.authenticate(
                    signInRequestDto.identifier,
                    signInRequestDto.password
                )
            )
        )
    }

    @PostMapping("/sign-out")
    fun signOut(): ResponseEntity<*> {
        return ResponseEntity.ok(mapOf("code" to 0, "message" to "로그아웃"))
    }
}

@RestController
class HomeApi {

    @GetMapping
    fun home(): ResponseEntity<*> {
        return ResponseEntity.ok(mapOf("code" to 0, "message" to "Hello, World!"))
    }
}