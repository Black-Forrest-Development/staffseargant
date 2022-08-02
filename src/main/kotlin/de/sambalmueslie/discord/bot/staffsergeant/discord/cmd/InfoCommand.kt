package de.sambalmueslie.discord.bot.staffsergeant.discord.cmd


import de.sambalmueslie.discord.bot.staffsergeant.discord.kotlin.applicationCommand
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.interaction.InteractionCreateEvent
import discord4j.core.spec.EmbedCreateFields
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.RestClient
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class InfoCommand : Command {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(InfoCommand::class.java)
        private const val CMD = "about"
    }

    override val name: String
        get() = CMD

    override fun register(restClient: RestClient, applicationId: Long) {
        val request = applicationCommand {
            name { CMD }
            description { "Show about info" }
        }

        restClient.applicationService.createGlobalApplicationCommand(applicationId, request).subscribe()
    }

    override fun <T : InteractionCreateEvent> matches(event: T): Boolean {
        return event is ChatInputInteractionEvent && event.commandName == CMD
    }

    override suspend fun <T : InteractionCreateEvent> process(event: T) {
        if (event is ChatInputInteractionEvent) {
            val builder = EmbedCreateSpec.builder()
            builder.author(EmbedCreateFields.Author.of("Staffsergeant", null, null))
            builder.title("Staff-sergeant BOT")
            builder.addField("Version", "1.0.0", false)
            builder.addField("Author", "IEE1394", false)
            event.reply().withEmbeds(builder.build()).withEphemeral(true).subscribe()
        }
    }


}
