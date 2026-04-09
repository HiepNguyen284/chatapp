package com.group4.chatapp.services

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class FcmTokenCleanupJob(
    private val fcmTokenService: FcmTokenService
) {

    @Scheduled(cron = "0 0 */6 * * *")
    fun pruneInactiveTokens() {
        fcmTokenService.pruneInactiveTokens()
    }
}
