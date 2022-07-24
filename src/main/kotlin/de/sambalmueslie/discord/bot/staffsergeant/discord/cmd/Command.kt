package de.sambalmueslie.discord.bot.staffsergeant.discord.cmd

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.rest.RestClient

interface Command {

    val name: String
    fun register(restClient: RestClient, applicationId: Long)
    fun matches(event: ChatInputInteractionEvent): Boolean
    suspend fun process(event: ChatInputInteractionEvent)
}
