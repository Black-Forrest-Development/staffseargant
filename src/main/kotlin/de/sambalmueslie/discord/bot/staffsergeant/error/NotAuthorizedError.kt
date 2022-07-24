package de.sambalmueslie.discord.bot.staffsergeant.error

import io.micronaut.security.authentication.Authentication

data class NotAuthorizedError(
    val auth: Authentication,
    val api: String,
    val role: String,
    val message: String
)
