package me.choicore.likeapuppy.authentication.jwt

import me.choicore.likeapuppy.authentication.common.Slf4j
import me.choicore.likeapuppy.authentication.exception.UnauthorizedException
import me.choicore.likeapuppy.authentication.repository.ephemeral.TokenRedisRepository
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.AuthenticationCredentials
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Credentials
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Principal
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@Component
class JwtAuthenticationConverter(
    private val tokenRedisRepository: TokenRedisRepository,
) : Converter<Jwt, AbstractAuthenticationToken> {
    private val log = Slf4j
    private var jwtGrantedAuthoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>> = JwtGrantedAuthoritiesConverter()
    private var principalClaimName: String = JwtClaimNames.SUB

    @Throws(UnauthorizedException::class)
    override fun convert(jwt: Jwt): AbstractAuthenticationToken {

        val authorities: Collection<GrantedAuthority>? = jwtGrantedAuthoritiesConverter.convert(jwt)

        val principal: Principal = getCachedUserPrincipal(jwt)

        return UsernamePasswordAuthenticationToken(principal, null, authorities)
    }

    fun setJwtGrantedAuthoritiesConverter(
        jwtGrantedAuthoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>>
    ) {
        Assert.notNull(jwtGrantedAuthoritiesConverter, "jwtGrantedAuthoritiesConverter cannot be null")
        this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter
    }

    fun setPrincipalClaimName(principalClaimName: String) {
        Assert.hasText(principalClaimName, "principalClaimName cannot be empty")
        this.principalClaimName = principalClaimName
    }

    @Throws(UnauthorizedException::class)
    private fun getCachedUserPrincipal(jwt: Jwt): Principal {
        log.info("Validating token with id: ${jwt.id}")

        val authenticationCredentials: AuthenticationCredentials = getAuthenticationCredentials(jwt.id)

        validateTokenWithCredentials(jwt, authenticationCredentials.credentials)

        return authenticationCredentials.principal
    }

    // If no credentials found in cache for the JWT id, it means the token is expired.
    private fun getAuthenticationCredentials(id: String): AuthenticationCredentials {
        return tokenRedisRepository.findById(id) ?: throw UnauthorizedException.ExpiredToken
    }


    // If neither access nor refresh tokens match the JWT's value,
    // it means the provided JWT is invalid.
    @Throws(UnauthorizedException::class)
    private fun validateTokenWithCredentials(jwt: Jwt, credentials: Credentials) {
        if (jwt.compareTokenValue(credentials.accessToken)) {
            log.debug("Compare requested to cached access token matched")
            return
        }

        if (jwt.compareTokenValue(credentials.refreshToken)) {
            log.debug("Compare requested to cached refresh token matched")
            return
        }

        log.error("Compare requested token to cached authentication token not matched")
        tokenRedisRepository.deleteById(jwt.id)
        throw UnauthorizedException.InvalidToken
    }


    private fun Jwt.compareTokenValue(tokenValue: String): Boolean {
        return this.tokenValue == tokenValue
    }
}

