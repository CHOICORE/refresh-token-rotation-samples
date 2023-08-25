package me.choicore.demo.springsecurity.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.RSAKey.Builder
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import me.choicore.demo.springsecurity.authentication.common.properties.RsaKeyProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder


@Configuration
class JwtConfiguration(
    private val rsaKeyProperties: RsaKeyProperties,
) {

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.publicKey).build()
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val rsaKey: RSAKey = Builder(rsaKeyProperties.publicKey)
            .privateKey(rsaKeyProperties.privateKey)
            .build()
        val jwkSet = JWKSet(rsaKey)
        return NimbusJwtEncoder(ImmutableJWKSet(jwkSet))
    }
}