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
    fun tokenSaveAndRetrievalFromRedis() {

        // given
        val uuid = UUID.randomUUID().toString()
        val stubTokenCache = AuthenticationTokenCache(
            key = uuid,
            value = AuthenticationCredentials(
                identifier = 1,
                credentials = Credentials(
                    accessToken = "accessToken",
                    refreshToken = "refreshToken",
                )
            ),
            ttl = 30L,
        )
        // when
        tokenRedisRepository.save(stubTokenCache)

        val authenticationCredentials: AuthenticationCredentials = tokenRedisRepository.findById(uuid)

        // then
        assertThat(authenticationCredentials).isNotNull()
        assertThat(stubTokenCache.value).isEqualTo(authenticationCredentials)
    }
}