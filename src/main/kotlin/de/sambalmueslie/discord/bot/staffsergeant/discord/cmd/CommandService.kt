package de.sambalmueslie.discord.bot.staffsergeant.discord.cmd


import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import jakarta.inject.Singleton
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class CommandService(
    private val commands: List<Command>
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(CommandService::class.java)
    }

    fun register(client: GatewayDiscordClient) {
        val restClient = client.restClient
        val applicationId = restClient.applicationId.block() ?: return logger.error("Not application id found")

        commands.forEach {
            logger.info("Register Command: [$it]")
            it.register(restClient, applicationId)
        }
    }

    suspend fun handleEvent(event: ChatInputInteractionEvent) {
        val cmd = commands.firstOrNull { it.matches(event) }
        if (cmd == null) {
            event.reply("Unknown command ${event.commandName}")
        } else {
            cmd.process(event)
        }
    }


}
