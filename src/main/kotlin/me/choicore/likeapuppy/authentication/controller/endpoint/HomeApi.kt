package me.choicore.likeapuppy.authentication.controller.endpoint

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class HomeApi {

    @GetMapping
    fun home(): ResponseEntity<*> {
        return ResponseEntity.ok(
            mapOf(
                "code" to 0,
                "message" to "Hello, World!"
            )
        )
    }
}