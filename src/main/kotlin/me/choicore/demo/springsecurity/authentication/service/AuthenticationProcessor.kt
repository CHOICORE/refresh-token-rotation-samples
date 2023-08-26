package me.choicore.demo.springsecurity.authentication.service

import me.choicore.demo.springsecurity.authentication.common.Slf4j
import me.choicore.demo.springsecurity.authentication.common.properties.AuthenticationProperties
import me.choicore.demo.springsecurity.authentication.exception.ExceededLoginAttemptsException
import me.choicore.demo.springsecurity.authentication.exception.InvalidPasswordException
import me.choicore.demo.springsecurity.authentication.exception.UnauthorizedException
import me.choicore.demo.springsecurity.authentication.exception.UsernameNotFoundException
import me.choicore.demo.springsecurity.authentication.jwt.AuthenticationToken
import me.choicore.demo.springsecurity.authentication.jwt.JwtAuthenticationTokenProvider
import me.choicore.demo.springsecurity.authentication.repository.persistence.UserEntity
import me.choicore.demo.springsecurity.authentication.repository.persistence.UserJpaRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


@Service
class AuthenticationProcessor(
    private val authenticationProperties: AuthenticationProperties,
    private val userJpaRepository: UserJpaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtAuthenticationTokenProvider: JwtAuthenticationTokenProvider,
) {

    private val log = Slf4j

    @Transactional(noRollbackFor = [UnauthorizedException::class])
    fun getAuthenticationToken(identifier: String, password: String): AuthenticationToken {
        log.info("Starting authentication for user with identifier $identifier.")

        val userEntity: UserEntity? = userJpaRepository.findByIdentifier(identifier = identifier)

        authenticate(userEntity, password)

        return issueTokenForValidatedUser(userEntity = userEntity).apply {
            log.info("Successfully issued token for user with identifier $identifier.")
        }
    }

    @OptIn(ExperimentalContracts::class)
    private fun authenticate(userEntity: UserEntity?, enteredPassword: String) {
        // Contract ensures that userEntity is not null when the function returns
        contract { returns() implies (userEntity != null) }

        // Throws an exception if userEntity is null, indicating that the username was not found
        userEntity ?: throw UsernameNotFoundException()

        // Checks if login is possible with the provided password from request
        userEntity.validateAuthentication(enteredPassword)

        // If validation succeeds, mark the user as logged in .
        userEntity.markAsLoggedIn()
    }


    /**
     * 패스워드가 일치하는지 확인하고, 일치하지 않으면 예외를 발생시킨다.
     */
    private fun UserEntity.validateAuthentication(enteredPassword: String) {

        if (failedLoginAttempts >= authenticationProperties.loginAttemptsLimit) {
            log.error("User with ID $id has exceeded the maximum number of login attempts.")
            throw ExceededLoginAttemptsException()
        }

        if (!passwordEncoder.matches(enteredPassword, this.password)) {
            failedLoginAttempts += 1
            log.error("Invalid password entered for user with ID $id.")
            throw InvalidPasswordException()
        }
        // TODO - if user is in dormant status, throw exception
    }

    /**
     * 로그인 성공 시, 패스워드 오류 횟수를 초기화하고 최근 로그인 시간을 갱신한다.
     */
    private fun UserEntity.markAsLoggedIn() {
        failedLoginAttempts = 0
        lastLoggedInAt = LocalDateTime.now()
    }

    private fun issueTokenForValidatedUser(userEntity: UserEntity): AuthenticationToken {
        return jwtAuthenticationTokenProvider.generateAuthenticationToken(identifier = userEntity.id)
    }
}
