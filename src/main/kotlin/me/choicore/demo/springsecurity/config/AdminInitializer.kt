package me.choicore.demo.springsecurity.config


import me.choicore.demo.springsecurity.authentication.repository.persistence.UserEntity
import me.choicore.demo.springsecurity.authentication.repository.persistence.UserJpaRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AdminInitializer(
    private val userJpaRepository: UserJpaRepository,
    private val passwordEncoder: PasswordEncoder,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        val userEntity = UserEntity(
            email = "admin",
            password = passwordEncoder.encode("admin"),
            mobile = "01012341234",
            nickname = "관리자",
            username = UserEntity.Username(
                firstName = "",
                lastName = "",
            ),
            gender = UserEntity.Gender.UNKNOWN,
        )
        userJpaRepository.save(userEntity)
    }
}