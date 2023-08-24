package me.choicore.demo.springsecurity.authentication.repository.ephemeral

import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.AuthenticationCredentials
import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.AuthenticationTokenStore
import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.Credentials
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.util.UUID


@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TokenRedisRepositoryTest(
    private val tokenRedisRepository: TokenRedisRepository,
) {
    @Test
    fun `save token to redis`() {

        // given
        val uuid = UUID.randomUUID().toString()
        val stub = AuthenticationTokenStore(
            key = uuid,
            value = AuthenticationCredentials(
                identifier = 1,
                credentials = Credentials(
                    accessToken = "eyJhbGciOiJSUzI1NiJ9",
                    refreshToken = "eyJhbGciOiJSUzI1NiJ9"
                )
            )
        )

        // when
        tokenRedisRepository.save(stub)
        val token = tokenRedisRepository.findById(uuid)

        // then
        assertThat(token).isNotNull()
        assertThat(token).isEqualTo(stub.value.toString())
    }
}