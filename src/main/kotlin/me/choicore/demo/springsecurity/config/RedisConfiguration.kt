package me.choicore.demo.springsecurity.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisConfiguration {

    @Bean
    fun redisConnectionFactory(
        @Value("\${spring.data.redis.host}") hostName: String,
        @Value("\${spring.data.redis.port}") port: Int,
    ): LettuceConnectionFactory = LettuceConnectionFactory(RedisStandaloneConfiguration(hostName, port))

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<*, *> {
        return RedisTemplate<Any, Any>().apply {
            this.connectionFactory = redisConnectionFactory
            this.keySerializer = StringRedisSerializer.UTF_8
            this.valueSerializer = StringRedisSerializer.UTF_8
        }
    }
}