package me.choicore.demo.springsecurity.config


import jakarta.persistence.EntityManager
import me.choicore.demo.springsecurity.authentication.repository.persistence.UserJpaRepository
import me.choicore.demo.springsecurity.authentication.repository.persistence.entity.UserEntity
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AdminInitializer(
    private val userJpaRepository: UserJpaRepository,
    private val em: EntityManager,
    private val passwordEncoder: PasswordEncoder
) : ApplicationRunner {

    @Transactional
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