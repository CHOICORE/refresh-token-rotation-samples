package me.choicore.demo.springsecurity.authentication.repository.ephemeral

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.AuthenticationCredentials
import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.AuthenticationTokenCache
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.RedisKeyExpiredEvent
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit


@Repository
class TokenRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private val valueOperations: ValueOperations<String, String>
        get() {
            return this.redisTemplate.opsForValue()
        }

    @EventListener(condition = "#authenticationTokenCache != null")
    fun save(authenticationTokenCache: AuthenticationTokenCache) {
        println("save: $authenticationTokenCache")
        val jsonString = objectMapper.writeValueAsString(authenticationTokenCache.value)
        val valueOperations1 = valueOperations
        valueOperations1[authenticationTokenCache.key] = jsonString
        valueOperations1.apply {
            getAndExpire(authenticationTokenCache.key, authenticationTokenCache.ttl, TimeUnit.SECONDS)
        }
    }

    @EventListener
    fun handleRedisKeyExpiredEvent(event: RedisKeyExpiredEvent<*>) {
        println("handleRedisKeyExpiredEvent: $event")
    }

    fun findById(id: String): AuthenticationCredentials {
        println("findById: $id")
        valueOperations[id]?.let {
            return objectMapper.readValue(it, AuthenticationCredentials::class.java)
        } ?: throw IllegalArgumentException("Not found token for id: $id")
    }
}

