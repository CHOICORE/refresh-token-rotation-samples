package me.choicore.demo.springsecurity.authentication.repository.ephemeral

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.choicore.demo.springsecurity.authentication.common.Slf4j
import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.AuthenticationCredentials
import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.AuthenticationTokenCache
import org.slf4j.Logger
import org.springframework.context.event.EventListener
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

    @EventListener(condition = "#authenticationTokenCache != null")
    fun save(authenticationTokenCache: AuthenticationTokenCache) {
        log.debug("save: {}", authenticationTokenCache)
        val valueOperations: ValueOperations<String, String> = valueOperations

        val jsonString = objectMapper.writeValueAsString(authenticationTokenCache.value)

        valueOperations[authenticationTokenCache.key] = jsonString

        valueOperations
            .apply {
                getAndExpire(authenticationTokenCache.key, authenticationTokenCache.ttl, TimeUnit.SECONDS)
            }
    }

    fun findById(id: String): AuthenticationCredentials? {
        log.info("findById: $id")
        return valueOperations[id]
            .let {
                objectMapper.readValue(it, AuthenticationCredentials::class.java)
            }
    }

    fun deleteById(id: String) {
        log.info("deleteById: $id")
        redisTemplate.delete(id)
    }
}

//        valueOperations[id]?.let {
//            return objectMapper.readValue(it, AuthenticationCredentials::class.java)
//        } ?: throw IllegalStateException("Not found token for id: $id")