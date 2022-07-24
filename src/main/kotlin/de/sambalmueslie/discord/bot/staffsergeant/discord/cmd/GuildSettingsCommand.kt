package de.sambalmueslie.discord.bot.staffsergeant.discord.cmd


import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.RestClient
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class GuildSettingsCommand : Command {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GuildSettingsCommand::class.java)
        private const val CMD = "settings"
    }

    override val name: String
        get() = CMD

    override fun register(restClient: RestClient, applicationId: Long) {
        val request: ApplicationCommandRequest = ApplicationCommandRequest.builder()
            .name(CMD)
            .description("Show the current settings")
            .build()

        restClient.applicationService.createGlobalApplicationCommand(applicationId, request).subscribe()
    }

    override fun matches(event: ChatInputInteractionEvent): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun process(event: ChatInputInteractionEvent) {
        TODO("Not yet implemented")
    }


}
