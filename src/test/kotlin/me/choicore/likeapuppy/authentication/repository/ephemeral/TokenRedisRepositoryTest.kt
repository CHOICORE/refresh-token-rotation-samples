package me.choicore.likeapuppy.authentication.repository.ephemeral

import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.AuthenticationCredentials
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.AuthenticationTokenCache
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Credentials
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.Principal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
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
    @DisplayName("토큰 정보를 Redis에 저장한다.")
    fun tokenSave() {

        // given
        val uuid = UUID.randomUUID().toString()
        val stubTokenCache = AuthenticationTokenCache(
            key = uuid,
            value = AuthenticationCredentials(
                principal = Principal(
                    identifier = 1L
                ),
                credentials = Credentials(
                    accessToken = "accessToken",
                    refreshToken = "refreshToken",
                )
            ),
            ttl = 30L,
        )
        // when
        tokenRedisRepository.save(stubTokenCache)
        val authenticationCredentials: AuthenticationCredentials? = tokenRedisRepository.findById(uuid)

        // then
        assertThat(authenticationCredentials).isNotNull()
        assertThat(stubTokenCache.value).isEqualTo(authenticationCredentials)
    }
}