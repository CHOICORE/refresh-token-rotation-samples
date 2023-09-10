package me.choicore.likeapuppy.authentication.jwt

import me.choicore.likeapuppy.authentication.common.Slf4j
import me.choicore.likeapuppy.authentication.exception.UnauthorizedException
import me.choicore.likeapuppy.authentication.repository.ephemeral.TokenRedisRepository
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.AuthenticationCredentials
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Identifier
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.util.Assert

class JwtAuthenticationConverter(
    private val tokenRedisRepository: TokenRedisRepository,
) : Converter<Jwt, AbstractAuthenticationToken> {
    private val log = Slf4j
    private var jwtGrantedAuthoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>> = JwtGrantedAuthoritiesConverter()
    private var principalClaimName: String = JwtClaimNames.SUB

    @Throws(UnauthorizedException::class)
    override fun convert(jwt: Jwt): AbstractAuthenticationToken {

        val authorities: Collection<GrantedAuthority>? = jwtGrantedAuthoritiesConverter.convert(jwt)

        val identifier: Identifier = getCachedUserIdentifier(jwt)

        // JwtAuthenticationToken
        return UsernamePasswordAuthenticationToken(identifier, null, authorities)
    }

    fun setJwtGrantedAuthoritiesConverter(
        jwtGrantedAuthoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>>,
    ) {
        Assert.notNull(jwtGrantedAuthoritiesConverter, "jwtGrantedAuthoritiesConverter cannot be null")
        this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter
    }

    fun setPrincipalClaimName(principalClaimName: String) {
        Assert.hasText(principalClaimName, "principalClaimName cannot be empty")
        this.principalClaimName = principalClaimName
    }

    @Throws(UnauthorizedException::class)
    private fun getCachedUserIdentifier(jwt: Jwt): Identifier {
        log.info("Validating token with id: ${jwt.id}")

        val authenticationCredentials: AuthenticationCredentials = tokenRedisRepository.findById(jwt.id)

        jwt.validateByCachedToken(authenticationCredentials.credentials.accessToken)

        return authenticationCredentials.principal.identifier
    }

    /**
     * Validate the JWT by comparing the access token in the cache.
     * it means the provided JWT is invalid.
     */
    @Throws(UnauthorizedException::class)
    private fun Jwt.validateByCachedToken(tokenValue: String) {
        if (this.tokenValue == tokenValue) {
            log.debug("Compare requested to cached access token matched")
            return
        }

        log.error("Compare requested token to cached authentication token not matched")
        tokenRedisRepository.deleteById(this.id)
        throw UnauthorizedException.InvalidToken
    }
}