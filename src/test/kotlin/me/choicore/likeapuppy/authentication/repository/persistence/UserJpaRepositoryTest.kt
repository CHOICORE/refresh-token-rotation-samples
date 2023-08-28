package me.choicore.likeapuppy.authentication.repository.persistence

import me.choicore.likeapuppy.authentication.repository.persistence.entity.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestConstructor


@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserJpaRepositoryTest(
    val userJpaRepository: UserJpaRepository,
) {

    @BeforeEach
    fun setUp() {
        val userEntity = UserEntity(
            email = "admin",
            password = "admin",
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

    @Test
    @DisplayName("이메일 또는 휴대폰 번호로 사용자 조회")
    fun findByIdentifier() {
        // given
        val email = "admin"
        val mobile = "01012341234"

        // when
        val foundUserByEmail: UserEntity? = userJpaRepository.findByIdentifier(email)
        val foundUserByMobile: UserEntity? = userJpaRepository.findByIdentifier(mobile)

        // then
        assertThat(foundUserByEmail).isEqualTo(foundUserByMobile)
    }
}