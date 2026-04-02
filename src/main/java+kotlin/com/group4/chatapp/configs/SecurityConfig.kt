package com.group4.chatapp.configs

import com.group4.chatapp.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig
@Autowired constructor(

    val jwtDecoder: JwtDecoder,
    val userRepository: UserRepository

) {

    private fun loadByUsername(username: String) =
        userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found") }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http {

            httpBasic { }
            oauth2ResourceServer { jwt { jwtDecoder = jwtDecoder } }

            csrf { disable() }

            cors {
                configurationSource = CorsConfigurationSource {
                    CorsConfiguration().apply {

                        HttpMethod.values()
                            .forEach { method -> addAllowedMethod(method) }

                        allowCredentials = true
                        addAllowedOriginPattern("*")

                        applyPermitDefaultValues()
                    }
                }
            }

            authorizeHttpRequests {
                authorize("/api/v1/messages/**", authenticated)
                authorize("/api/v1/chatbot/**", authenticated)
                authorize("/api/v1/invitations/**", authenticated)
                authorize("/api/v1/users/me/**", authenticated)
                authorize("/api/v1/users/blocks/**", authenticated)
                authorize(HttpMethod.POST, "/api/v1/users/*/block/", authenticated)
                authorize(HttpMethod.DELETE, "/api/v1/users/*/block/", authenticated)
                authorize(HttpMethod.GET, "/api/v1/users/*/block-status/", authenticated)
                authorize(HttpMethod.GET, "/api/v1/users/*/presence/", authenticated)
                authorize(anyRequest, permitAll)
            }
        }

        return http.build()
    }

    @Bean
    fun passwordEncoder() = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

    @Bean
    fun daoAuthenticationProvider(passwordEncoder: PasswordEncoder) =
        DaoAuthenticationProvider(this::loadByUsername)
            .apply { setPasswordEncoder(passwordEncoder) }

    @Bean
    fun jwtAuthenticationProvider() = JwtAuthenticationProvider(jwtDecoder)

    @Bean
    fun authenticationManager(providers: List<AuthenticationProvider>) =
        ProviderManager(providers)
}