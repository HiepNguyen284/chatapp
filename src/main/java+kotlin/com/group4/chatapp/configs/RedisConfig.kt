package com.group4.chatapp.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
class RedisConfig {

    @Value("\${spring.data.redis.url:redis://localhost:6379}")
    private lateinit var redisUrl: String

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val uri = redisUrl.removePrefix("redis://")
        val parts = uri.split(":")
        val host = parts[0]
        val port = parts[1].toInt()

        val config = RedisStandaloneConfiguration(host, port)
        return LettuceConnectionFactory(config)
    }

    @Bean
    fun stringRedisTemplate(connectionFactory: RedisConnectionFactory): StringRedisTemplate {
        return StringRedisTemplate(connectionFactory)
    }
}
