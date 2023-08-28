package me.choicore.demo.springsecurity.authentication.service

import me.choicore.demo.springsecurity.authentication.common.Slf4j
import me.choicore.demo.springsecurity.authentication.common.properties.AuthenticationProperties
import me.choicore.demo.springsecurity.authentication.exception.UnauthorizedException
import me.choicore.demo.springsecurity.authentication.jwt.AuthenticationToken
import me.choicore.demo.springsecurity.authentication.jwt.JwtAuthenticationTokenProvider
import me.choicore.demo.springsecurity.authentication.repository.persistence.UserJpaRepository
import me.choicore.demo.springsecurity.authentication.repository.persistence.entity.UserEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@Repository
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

    @Throws(UnauthorizedException::class)
    private fun UserEntity.validateAuthentication(enteredPassword: String) {

        if (failedLoginAttempts >= authenticationProperties.loginAttemptsLimit) {
            log.error("User with ID $id has exceeded the maximum number of login attempts.")
            throw UnauthorizedException.LoginAttemptsExceeded
        }

        if (!passwordEncoder.matches(enteredPassword, password)) {
            failedLoginAttempts += 1
            log.error("Invalid password entered for user with ID $id.")
            throw UnauthorizedException.InvalidPassword
        }
        if (isDormant()) {
            log.error("User with ID $id has not logged in for over a year and is now dormant.")
            throw UnauthorizedException.DormantAccount
        }
    }

    private fun UserEntity.isDormant(): Boolean {
        if (lastLoggedInAt == null) return false
        val lastLoggedInAt: ZonedDateTime = ZonedDateTime.ofInstant(lastLoggedInAt, ZoneId.systemDefault())
        return lastLoggedInAt.plusYears(1).isBefore(ZonedDateTime.now())
    }

    private fun UserEntity.markAsLoggedIn() {
        failedLoginAttempts = 0
        lastLoggedInAt = Instant.now()
    }

    private fun issueTokenForValidatedUser(userEntity: UserEntity): AuthenticationToken {
        return jwtAuthenticationTokenProvider.generateAuthenticationToken(identifier = userEntity.id)
    }
}