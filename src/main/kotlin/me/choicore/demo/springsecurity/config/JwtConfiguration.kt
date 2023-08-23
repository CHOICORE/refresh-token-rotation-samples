package me.choicore.demo.springsecurity.config

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
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
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyProperties.publicKey).build()

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk: JWK = with(rsaKeyProperties) {
            Builder(publicKey)
                .privateKey(privateKey)
                .build()
        }
        return NimbusJwtEncoder(ImmutableJWKSet(JWKSet(jwk)))
    }
}