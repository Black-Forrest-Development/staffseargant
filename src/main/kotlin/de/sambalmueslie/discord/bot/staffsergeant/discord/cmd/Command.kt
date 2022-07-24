package de.sambalmueslie.discord.bot.staffsergeant.discord.cmd

import discord4j.core.event.domain.interaction.InteractionCreateEvent
import discord4j.rest.RestClient

interface Command {

    val name: String
    fun register(restClient: RestClient, applicationId: Long)
    fun <T : InteractionCreateEvent> matches(event: T): Boolean
    suspend fun <T : InteractionCreateEvent> process(event: T)
}
