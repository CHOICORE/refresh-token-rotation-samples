package me.choicore.demo.springsecurity.authentication.jwt

import me.choicore.demo.springsecurity.authentication.common.properties.JwtProperties
import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.AuthenticationCredentials
import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.AuthenticationTokenStore
import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.Credentials
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component

class JwtAuthenticationTokenProvider(
    private val jwtProperties: JwtProperties,
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    private lateinit var jwt: Jwt
    fun validateToken(jwtToken: String): Boolean {
        return try {
            this.jwt = jwtDecoder.decode(jwtToken)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getIdentifier(): String {
        return jwt.id
    }

    fun issueTokens(id: String): Pair<String, String> {
        val accessToken = this.issueAccessToken(id)
        val refreshToken = this.issueRefreshToken(id)
        return Pair(accessToken, refreshToken)
    }

    fun generateAuthenticationToken(identifier: Long): AuthenticationToken {
        with(getAuthenticationTokenStore(identifier = identifier)) {
            applicationEventPublisher.publishEvent(this)
            return AuthenticationToken.bearerToken(
                accessToken = value.credentials.accessToken,
                expiresIn = jwtProperties.expiration.accessToken,
                refreshToken = value.credentials.refreshToken,
            )
        }
    }

    fun getAuthenticationTokenStore(identifier: Long): AuthenticationTokenStore {
        val uuid = UUID.randomUUID().toString()
        val issueTokens: Pair<String, String> = this.issueTokens(id = uuid)

        with(issueTokens) {
            return AuthenticationTokenStore(
                key = uuid,
                value = AuthenticationCredentials(
                    identifier = identifier,
                    credentials = Credentials(
                        accessToken = this.first,
                        refreshToken = this.second
                    )
                )
            )
        }
    }

    private fun issueRefreshToken(id: String): String {
        return this.generateToken(id = id, expiresAt = jwtProperties.expiration.refreshToken)
    }

    private fun issueAccessToken(id: String): String {
        return this.generateToken(id = id, expiresAt = jwtProperties.expiration.accessToken)
    }

    private fun generateToken(id: String, expiresAt: Long): String {
        val now: Instant = Instant.now()
        val jwtClaimsSet = JwtClaimsSet.builder()
            .id(id)
            .expiresAt(now.plusSeconds(expiresAt))
            .issuedAt(now)
            .issuer(jwtProperties.issuer)
            .build()
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).tokenValue
    }
}