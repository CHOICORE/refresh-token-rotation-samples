package me.choicore.demo.springsecurity.authentication.service

import me.choicore.demo.springsecurity.authentication.repository.persistence.UserEntity
import me.choicore.demo.springsecurity.authentication.repository.persistence.UserJpaRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthenticationProcessor(
    private val userJpaRepository: UserJpaRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun authenticate(identifier: String, password: String): UserEntity {
        val userEntity = getUserByIdentifier(identifier)
        validateUserPassword(password, userEntity.password)
        return userEntity
    }

    private fun getUserByIdentifier(identifier: String): UserEntity {
        return userJpaRepository.findByIdentifier(identifier)
            ?: throw IllegalArgumentException("사용자 정보가 올바르지 않습니다. 다시 확인해주세요.")
    }

    private fun validateUserPassword(
        rawPassword: String,
        encodedPassword: String,
    ) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw IllegalArgumentException("사용자 정보가 올바르지 않습니다. 다시 확인해주세요.")
        }
    }
}
