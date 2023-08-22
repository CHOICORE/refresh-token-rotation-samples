package me.choicore.demo.springsecurity.config

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.getAuthenticationManager()
    }

    private companion object {
        private const val API_END_POINT_PREFIX = "/v1"
        private val PERMIT_WHITE_LIST = arrayOf(
            PathRequest.toH2Console(),
            AntPathRequestMatcher("${API_END_POINT_PREFIX}/account/sign-in"),
            AntPathRequestMatcher("${API_END_POINT_PREFIX}/account/sign-out"),
            AntPathRequestMatcher("${API_END_POINT_PREFIX}/account/sign-up"),
        )
    }

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf { it.disable() }
            .headers { it.frameOptions { frameOption -> frameOption.sameOrigin() } }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(*PERMIT_WHITE_LIST).permitAll()
                it.anyRequest().authenticated()
            }

        return httpSecurity.build()
    }
}