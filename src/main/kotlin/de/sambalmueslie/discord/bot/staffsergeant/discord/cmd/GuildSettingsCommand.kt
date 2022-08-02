package de.sambalmueslie.discord.bot.staffsergeant.discord.cmd


import de.sambalmueslie.discord.bot.staffsergeant.discord.kotlin.applicationCommand
import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.interaction.InteractionCreateEvent
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.Button
import discord4j.core.`object`.component.LayoutComponent
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

        val request = applicationCommand {
            name { CMD }
            description { "Show the current settings" }
        }

        restClient.applicationService.createGlobalApplicationCommand(applicationId, request).subscribe()
    }

    override fun <T : InteractionCreateEvent> matches(event: T): Boolean {
        return (event is ChatInputInteractionEvent && event.commandName == CMD) || (event is ButtonInteractionEvent)
    }

    override suspend fun <T : InteractionCreateEvent> process(event: T) {
        when (event) {
            is ChatInputInteractionEvent -> process(event)
            is ButtonInteractionEvent -> process(event)
        }
    }

    private fun process(event: ChatInputInteractionEvent) {
        event.reply()
            .withComponents(createResponse(event))
            .withEphemeral(true)
            .subscribe()
    }

    private fun createResponse(event: ChatInputInteractionEvent): LayoutComponent {
        val button = Button.primary("custom-id", "Click me!!")
        return ActionRow.of(
            button
        )
    }

    private fun process(event: ButtonInteractionEvent) {
        event.reply("Your clicked ${event.customId}").withEphemeral(true).subscribe()
    }

}
