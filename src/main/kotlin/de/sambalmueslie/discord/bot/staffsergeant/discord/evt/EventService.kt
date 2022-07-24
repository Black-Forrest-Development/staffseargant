package de.sambalmueslie.discord.bot.staffsergeant.discord.evt


import de.sambalmueslie.discord.bot.staffsergeant.discord.cmd.CommandService
import de.sambalmueslie.discord.bot.staffsergeant.discord.processor.RegisterBotProcessor
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import discord4j.core.event.domain.guild.GuildCreateEvent
import discord4j.core.event.domain.guild.MemberUpdateEvent
import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import jakarta.inject.Singleton
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class EventService(
    private val commandService: CommandService,
    private val registerBotProcessor: RegisterBotProcessor
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(EventService::class.java)
    }


    fun register(client: GatewayDiscordClient) {
        subscribe(client, MemberUpdateEvent::class.java)
        subscribe(client, ChatInputInteractionEvent::class.java)
        subscribe(client, GuildCreateEvent::class.java)
        subscribe(client, ButtonInteractionEvent::class.java)
    }

    private fun <T : Event> subscribe(client: GatewayDiscordClient, eventType: Class<T>) {
        mono {
            client.on(eventType).asFlow().collect { handleEvent(it) }
        }.subscribe()
    }

    private suspend fun handleEvent(event: Event) {
        if (logger.isTraceEnabled) logger.trace("Received event $event")
        when (event) {
            is MemberUpdateEvent -> handleEvent(event)
            is ChatInputInteractionEvent -> handleEvent(event)
            is GuildCreateEvent -> handleEvent(event)
            is ButtonInteractionEvent -> handleEvent(event)
        }
    }

    private suspend fun handleEvent(event: MemberUpdateEvent) {
        // TODO not implemented yet
    }

    private suspend fun handleEvent(event: ChatInputInteractionEvent) {
        commandService.handleEvent(event)
    }

    private suspend fun handleEvent(event: GuildCreateEvent) {
        registerBotProcessor.handleEvent(event)
    }

    private suspend fun handleEvent(event: ButtonInteractionEvent) {
        commandService.handleEvent(event)
    }

}
