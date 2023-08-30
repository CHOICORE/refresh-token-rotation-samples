package me.choicore.likeapuppy.authentication.repository.ephemeral

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.choicore.likeapuppy.authentication.common.Slf4j
import me.choicore.likeapuppy.authentication.exception.UnauthorizedException
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.AuthenticationCredentials
import me.choicore.likeapuppy.authentication.repository.ephemeral.entity.AuthenticationTokenCache
import org.slf4j.Logger
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit


@Repository
class TokenRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private val log: Logger = Slf4j
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private val valueOperations: ValueOperations<String, String>
        get() {
            return this.redisTemplate.opsForValue()
        }

    fun save(authenticationTokenCache: AuthenticationTokenCache) {
        log.debug("Save: {}", authenticationTokenCache)
        val valueOperations: ValueOperations<String, String> = valueOperations

        val jsonString = objectMapper.writeValueAsString(authenticationTokenCache.value)

        valueOperations[authenticationTokenCache.key] = jsonString

        valueOperations
            .apply {
                getAndExpire(authenticationTokenCache.key, authenticationTokenCache.ttl, TimeUnit.SECONDS)
            }
    }

    /**
     * If no credentials found in cache for the JWT id, it means the token is expired.
     */
    @Throws(UnauthorizedException.InvalidToken::class)
    fun findById(id: String): AuthenticationCredentials {
        log.info("Find by id: $id")
        valueOperations[id]?.let {
            return objectMapper.readValue(it, AuthenticationCredentials::class.java)
        } ?: throw UnauthorizedException.InvalidToken
    }

    fun deleteById(id: String) {
        log.info("Delete by id: $id")
        val isDeleted: Boolean = redisTemplate.delete(id)

        check(isDeleted) {
            "Failed to delete token with id: $id"
        }
    }

    fun deleteAll() {
        log.info("Delete all")
        redisTemplate.delete(redisTemplate.keys("*"))
    }
}

//        valueOperations[id]?.let {
//            return objectMapper.readValue(it, AuthenticationCredentials::class.java)
//        } ?: throw IllegalStateException("Not found token for id: $id")