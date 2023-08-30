package me.choicore.likeapuppy.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.choicore.likeapuppy.authentication.exception.AuthenticationEntryPoint
import me.choicore.likeapuppy.authentication.jwt.JwtAuthenticationConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class DefaultSecurityConfigurer {

    private companion object {
        private const val API_END_POINT_PREFIX = "/v1"
        private val PERMIT_WHITE_LIST = arrayOf(
            AntPathRequestMatcher("$API_END_POINT_PREFIX/accounts/sign-in"),
            AntPathRequestMatcher("$API_END_POINT_PREFIX/auth/token"),
        )
    }

    @Bean
    fun objectMapper(): ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(
        httpSecurity: HttpSecurity,
        jwtAuthenticationConverter: JwtAuthenticationConverter,
    ): SecurityFilterChain {
        httpSecurity
            .oauth2ResourceServer { oauth2ResourceServer ->
                oauth2ResourceServer.jwt { configurer ->
                    configurer.jwtAuthenticationConverter(
                        jwtAuthenticationConverter.apply { setPrincipalClaimName("jti") })
                }
                oauth2ResourceServer.authenticationEntryPoint(AuthenticationEntryPoint(objectMapper()))
            }
            .csrf { it.disable() }
            .headers { it.frameOptions { frameOption -> frameOption.sameOrigin() } }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorizeHttpRequests ->
                authorizeHttpRequests.requestMatchers(*PERMIT_WHITE_LIST).permitAll()
                authorizeHttpRequests.anyRequest().authenticated()
            }

            .exceptionHandling {
                it.authenticationEntryPoint(AuthenticationEntryPoint(objectMapper()))
            }

        return httpSecurity.build()
    }
}
