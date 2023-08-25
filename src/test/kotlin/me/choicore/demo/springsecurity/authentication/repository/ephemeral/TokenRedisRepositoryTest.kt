package me.choicore.demo.springsecurity.authentication.repository.ephemeral

import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.AuthenticationCredentials
import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.AuthenticationTokenCache
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
        val stub = AuthenticationTokenCache(
            key = uuid,
            value = AuthenticationCredentials(
                identifier = 1,
                credentials = Credentials(
                    accessToken = "eyJhbGciOiJSUzI1NiJ9",
                    refreshToken = "eyJhbGciOiJSUzI1NiJ9"
                )
            ),
            ttl = 30L,
        )
        // when
        tokenRedisRepository.save(stub)
        val authenticationCredentials = tokenRedisRepository.findById(uuid)
        // then
        assertThat(authenticationCredentials).isNotNull()
        assertThat(stub.value).isEqualTo(authenticationCredentials)
    }
}