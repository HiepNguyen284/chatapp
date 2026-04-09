package com.group4.chatapp

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ChatApplication

fun main(args: Array<String>) {
    runApplication<ChatApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
