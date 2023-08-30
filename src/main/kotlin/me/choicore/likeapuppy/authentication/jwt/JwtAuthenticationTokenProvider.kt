package me.choicore.likeapuppy.authentication.jwt

import me.choicore.likeapuppy.authentication.common.Slf4j
import me.choicore.likeapuppy.authentication.common.properties.JwtProperties
import me.choicore.likeapuppy.authentication.exception.UnauthorizedException
import me.choicore.likeapuppy.authentication.repository.ephemeral.TokenRedisRepository
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.AuthenticationCredentials
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.AuthenticationTokenCache
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Credentials
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Identifier
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Principal
import org.slf4j.Logger
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.stereotype.Component
import java.time.Duration
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

    fun revocationToken(
        key: String,
    ) {
        tokenRedisRepository.deleteById(key)
    }

    fun generateAuthenticationToken(
        identifier: Long,
    ): AuthenticationToken {
        with(getAuthenticationTokenCache(identifier = identifier)) {
            return AuthenticationToken.bearerToken(
                accessToken = value.credentials.accessToken,
                expiresIn = jwtProperties.accessExpiresIn,
                refreshToken = value.credentials.refreshToken,
            )
        }
    }

    fun refreshTokenRotation(
        refreshToken: String,
    ): AuthenticationToken {
        val jwt: Jwt = validateTokenValue(tokenValue = refreshToken)
        val jti: String = jwt.id
        val authenticationCredentials: AuthenticationCredentials = tokenRedisRepository.findById(jti)

        if (jwt.tokenValue != authenticationCredentials.credentials.refreshToken) {
            throw UnauthorizedException.InvalidToken
        }
        val now: Instant = Instant.now()
        val expiresAt: Instant = jwt.expiresAt ?: throw IllegalStateException("Refresh token must have an expiration date")
        // Calculate remaining time in milliseconds
        val remainingTimeToLive: Long = Duration.between(now, expiresAt).toSeconds()

        // If the remaining time is less than the refresh threshold, issue a new refresh token
        val issuedAccessToken: String = issueAccessToken(jti = jti)
        val issuedRefreshToken: String = generateToken(
            jti = jti,
            expiresAt = now.plusSeconds(remainingTimeToLive),
            issuedAt = now,
        )
        val identifier: Long = authenticationCredentials.principal.identifier.private
        val authenticationTokenCache: AuthenticationTokenCache = createAuthenticationTokenCache(
            jti,
            identifier,
            issuedAccessToken,
            issuedRefreshToken,
            remainingTimeToLive
        ).apply {
            tokenRedisRepository.save(this)
        }
        return AuthenticationToken.bearerToken(
            accessToken = authenticationTokenCache.value.credentials.accessToken,
            expiresIn = jwtProperties.accessExpiresIn,
            refreshToken = authenticationTokenCache.value.credentials.refreshToken,
        )
    }

    fun generateToken(
        jti: String,
        issuer: String? = jwtProperties.issuer,
        subject: String? = null,
        expiresAt: Instant,
        issuedAt: Instant,
        claims: Map<String, Any>? = emptyMap(),
    ): String {
        val jwtClaimsSet: JwtClaimsSet = JwtClaimsSet.builder()
            .id(jti)
            .expiresAt(expiresAt)
            .issuedAt(issuedAt)
            .issuer(issuer)
            .claims {
                if (claims != null) {
                    it.putAll(claims)
                }
            }
            .build()
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).tokenValue
    }

    private fun getAuthenticationTokenCache(
        identifier: Long,
    ): AuthenticationTokenCache {
        val uuid: String = UUID.randomUUID().toString()
        val issueTokens: Pair<String, String> = this.issueTokens(jti = uuid)

        with(issueTokens) {
            return createAuthenticationTokenCache(
                uuid,
                identifier,
                this.first,
                this.second,
                jwtProperties.refreshExpiresIn
            ).apply {
                tokenRedisRepository.save(this)
            }
        }
    }

    private fun createAuthenticationTokenCache(
        uuid: String,
        identifier: Long,
        accessToken: String,
        refreshToken: String,
        ttl: Long?,
    ) = AuthenticationTokenCache(
        key = uuid,
        value = AuthenticationCredentials(
            principal = Principal(
                identifier = Identifier(
                    public = uuid,
                    private = identifier,
                ),
            ),
            credentials = Credentials(
                accessToken = accessToken,
                refreshToken = refreshToken,
            ),
        ),
        ttl = ttl ?: jwtProperties.refreshExpiresIn
    )

    private fun issueTokens(
        jti: String,
    ): Pair<String, String> {
        val accessToken: String = issueAccessToken(jti)
        val refreshToken: String = issueRefreshToken(jti)
        return Pair(
            accessToken,
            refreshToken,
        )
    }

    @Throws(AuthenticationException::class)
    fun validateTokenValue(
        tokenValue: String,
    ): Jwt {
        return try {
            jwtDecoder.decode(tokenValue)
        } catch (failed: BadJwtException) {
            log.debug("Failed to authenticate since the JWT was invalid")
            throw InvalidBearerTokenException(failed.message, failed)
        } catch (failed: JwtException) {
            throw AuthenticationServiceException(failed.message, failed)
        }
    }

    private fun issueRefreshToken(
        jti: String,
    ): String {
        val issuedAt: Instant = Instant.now()
        return generateToken(
            jti = jti,
            expiresAt = issuedAt.plusSeconds(jwtProperties.refreshExpiresIn),
            issuedAt = issuedAt
        )
    }

    private fun issueAccessToken(
        jti: String,
    ): String {
        val issuedAt: Instant = Instant.now()
        return generateToken(
            jti = jti,
            expiresAt = issuedAt.plusSeconds(jwtProperties.accessExpiresIn),
            issuedAt = issuedAt,
        )
    }
}
