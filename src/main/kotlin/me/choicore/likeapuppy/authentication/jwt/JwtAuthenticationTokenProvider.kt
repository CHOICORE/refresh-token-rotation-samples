package me.choicore.likeapuppy.authentication.jwt

import me.choicore.likeapuppy.authentication.common.Slf4j
import me.choicore.likeapuppy.authentication.common.properties.JwtProperties
import me.choicore.likeapuppy.authentication.repository.ephemeral.TokenRedisRepository
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.AuthenticationCredentials
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.AuthenticationTokenCache
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Credentials
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Principal
import org.slf4j.Logger
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
    private val tokenRedisRepository: TokenRedisRepository,
) {
    private val log: Logger = Slf4j
    private lateinit var jwt: Jwt

    fun validateToken(jwtToken: String): Boolean {
        return try {
            this.jwt = jwtDecoder.decode(jwtToken)
            true
        } catch (e: Exception) {
            log.error("Invalid token: {}", jwtToken)
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
        with(getAuthenticationTokenCache(identifier = identifier)) {
            return AuthenticationToken.bearerToken(
                accessToken = value.credentials.accessToken,
                expiresIn = jwtProperties.accessExpiredAt,
                refreshToken = value.credentials.refreshToken,
            )
        }
    }

    private fun getAuthenticationTokenCache(identifier: Long): AuthenticationTokenCache {
        val uuid = UUID.randomUUID().toString()
        val issueTokens: Pair<String, String> = this.issueTokens(id = uuid)

        with(issueTokens) {
            return AuthenticationTokenCache(
                key = uuid,
                value = AuthenticationCredentials(
                    principal = Principal(
                        identifier = identifier
                    ),
                    credentials = Credentials(
                        accessToken = this.first,
                        refreshToken = this.second
                    )
                ),
                ttl = jwtProperties.refreshExpiredAt
            ).apply {
                tokenRedisRepository.save(this)
            }
        }
    }

    fun reissueToken(refreshToken: String): AuthenticationToken {
        TODO()
    }

    private fun issueRefreshToken(id: String): String {
        return this.generateToken(id = id, expiresAt = jwtProperties.refreshExpiredAt)
    }

    private fun issueAccessToken(id: String): String {
        return this.generateToken(id = id, expiresAt = jwtProperties.accessExpiredAt)
    }

    fun generateToken(id: String, expiresAt: Long): String {
        return generateToken(id = id, expiresAt = expiresAt, issuedAt = Instant.now())
    }

    fun generateToken(id: String, expiresAt: Long, issuedAt: Instant): String {
        val jwtClaimsSet = JwtClaimsSet.builder()
            .id(id)
            .expiresAt(issuedAt.plusSeconds(expiresAt))
            .issuedAt(issuedAt)
            .issuer(jwtProperties.issuer)
            .build()
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).tokenValue
    }
}