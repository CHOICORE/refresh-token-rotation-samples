package me.choicore.demo.springsecurity.authentication.service

import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class JwtTokenProvider(
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
) {
    private companion object {
        const val TOKEN_ISSUER = "AUTHENTICATOR"
    }

    fun generateToken(identifier: Long): String {
        val now: Instant = Instant.now()

        val jwtClaimsSet = JwtClaimsSet.builder()
            .id(UUID.randomUUID().toString())
            .subject(identifier.toString())
            .expiresAt(now.plusSeconds(3600))
            .issuedAt(now)
            .issuer(TOKEN_ISSUER)
            .build()
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).tokenValue
    }
}