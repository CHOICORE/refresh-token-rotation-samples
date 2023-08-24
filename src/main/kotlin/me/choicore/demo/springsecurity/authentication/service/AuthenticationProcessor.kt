package me.choicore.demo.springsecurity.authentication.service

import me.choicore.demo.springsecurity.authentication.common.properties.AuthenticationProperties
import me.choicore.demo.springsecurity.authentication.exception.ExceededLoginAttemptsException
import me.choicore.demo.springsecurity.authentication.exception.InvalidPasswordException
import me.choicore.demo.springsecurity.authentication.exception.UnauthorizedException
import me.choicore.demo.springsecurity.authentication.exception.UsernameNotFoundException
import me.choicore.demo.springsecurity.authentication.jwt.AuthenticationToken
import me.choicore.demo.springsecurity.authentication.jwt.JwtAuthenticationTokenProvider
import me.choicore.demo.springsecurity.authentication.repository.persistence.UserEntity
import me.choicore.demo.springsecurity.authentication.repository.persistence.UserJpaRepository
import me.choicore.demo.springsecurity.authentication.service.domain.loginAttemptsExceeded
import me.choicore.demo.springsecurity.authentication.service.domain.loginFailure
import me.choicore.demo.springsecurity.authentication.service.domain.updateStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthenticationProcessor(
    private val authenticationProperties: AuthenticationProperties,
    private val userJpaRepository: UserJpaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtAuthenticationTokenProvider: JwtAuthenticationTokenProvider,
) {
    @Transactional(noRollbackFor = [UnauthorizedException::class])
    fun authenticate(identifier: String, password: String): AuthenticationToken {
        with(receiver = userJpaRepository.findByIdentifier(identifier = identifier) ?: throw UsernameNotFoundException()) {
            // 로그인 실패 횟수 체크
            if (this.loginAttemptsExceeded(authenticationProperties.loginAttemptsLimit)) {
                throw ExceededLoginAttemptsException()
            }

            // 패스워드 검증
            check(checkPassword(rawPassword = password, encodedPassword = this.password)) {
                this.loginFailure()
                throw InvalidPasswordException()
            }

            this.updateStatus()

            // 토큰 발급
            return issueTokenForValidatedUser(userEntity = this)
        }
    }

    private fun checkPassword(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }

    private fun issueTokenForValidatedUser(userEntity: UserEntity): AuthenticationToken {
        with(jwtAuthenticationTokenProvider.generateAuthenticationToken(identifier = userEntity.id)) {
            return this
        }
    }
}