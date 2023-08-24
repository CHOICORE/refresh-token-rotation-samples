package me.choicore.demo.springsecurity.authentication.repository.ephemeral

import me.choicore.demo.springsecurity.authentication.repository.ephemeral.entity.AuthenticationTokenStore
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository


@Repository
class TokenRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    @EventListener(condition = "#authenticationTokenStore != null")
    fun save(authenticationTokenStore: AuthenticationTokenStore) {
        println("save: $authenticationTokenStore")
        this.redisTemplate.opsForValue().set(authenticationTokenStore.key, authenticationTokenStore.value.toString())
    }

    fun findById(id: String): String {
        println("findById: $id")
        this.redisTemplate.opsForValue().get(id)?.let {
            return it
        } ?: throw IllegalArgumentException("Not found token for id: $id")
    }
}