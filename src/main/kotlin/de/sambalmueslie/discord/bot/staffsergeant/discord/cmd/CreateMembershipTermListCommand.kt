package de.sambalmueslie.discord.bot.staffsergeant.discord.cmd


import de.sambalmueslie.discord.bot.staffsergeant.discord.kotlin.applicationCommand
import de.sambalmueslie.discord.bot.staffsergeant.discord.kotlin.option
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.interaction.InteractionCreateEvent
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import discord4j.core.`object`.command.ApplicationCommandInteractionOptionValue
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.InteractionFollowupCreateSpec
import discord4j.rest.RestClient
import discord4j.rest.util.Color
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset

@Singleton
class CreateMembershipTermListCommand : Command {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ExportRolesCommand::class.java)
        private const val CMD = "membership_term_list"
        private const val OPTION_FILTER = "filter"
        private const val OPTION_PUBLIC = "public"
        private const val OPTION_LENGTH = "length"
    }

    override val name: String
        get() = CMD

    override fun register(restClient: RestClient, applicationId: Long) {
        val request = applicationCommand {
            name { CMD }
            description { "Prints a list of member ordered by join date" }
            options {
                option {
                    name { OPTION_FILTER }
                    description { "A role name regex filter" }
                    type { ApplicationCommandOption.Type.STRING.value }
                    required { false }
                }
                option {
                    name { OPTION_LENGTH }
                    description { "The length of the list" }
                    type { ApplicationCommandOption.Type.INTEGER.value }
                    required { false }
                }
                option {
                    name { OPTION_PUBLIC }
                    description { "Public or personal response (default=public)" }
                    type { ApplicationCommandOption.Type.BOOLEAN.value }
                    required { false }
                }
            }
        }

        restClient.applicationService.createGlobalApplicationCommand(applicationId, request).subscribe()
    }

    override fun <T : InteractionCreateEvent> matches(event: T): Boolean {
        return event is ChatInputInteractionEvent && event.commandName == CMD
    }


    override suspend fun <T : InteractionCreateEvent> process(event: T) {
        if (event is ChatInputInteractionEvent) {
            val ephemeral = getOptionPublic(event)
            logger.debug("Public reply = ${!ephemeral}")

            event.deferReply()
                .withEphemeral(ephemeral)
                .then(handleCreateListCommand(event))
                .doOnError { error -> ExportRolesCommand.logger.error("Failed to respond", error) }
                .subscribe()
        }
    }

    @Suppress("ReactiveStreamsUnusedPublisher")
    private suspend fun handleCreateListCommand(event: ChatInputInteractionEvent): Mono<Message> {
        val guild = event.interaction.guild.awaitSingle() ?: return event.createFollowup("Cannot find guild")
        logger.info("Show roles for ${guild.name}")

        val filter = getOptionFilter(event)
        val maxLength = getOptionLength(event)
        val members = withContext(Dispatchers.IO) {
            guild.members.toIterable()
                .filter { it.displayName.matches(filter) }
                .sortedBy { it.joinTime.orElseGet { Instant.now() } }
                .take(maxLength.toInt())
        }
        val content = StringBuilder()

        var current = 10
        var length = 1
        while (current > members.size && length < 10) {
            current *= 10
            length++
        }

        val now = LocalDate.now()
        members.forEachIndexed { index, member ->
            content.append(index.plus(1).toString().padStart(length, '0')).append(". ")
            content.append(member.displayName)
            val period = Period.between(LocalDate.ofInstant(member.joinTime.orElseGet { Instant.now() }, ZoneOffset.systemDefault()), now)
            content.append(" since ")
            content.append("${period.years}Y ")
            content.append("${period.months}M ")
            content.append("${period.days}D")
            content.appendLine()
        }


        val embed = EmbedCreateSpec.builder()
            .color(Color.JAZZBERRY_JAM)
            .title("Member by Join Date")
            .description(content.toString())
            .timestamp(Instant.now())
            .build()

        val spec = InteractionFollowupCreateSpec.builder().addEmbed(embed)
        return event.createFollowup(spec.build())
    }


    private fun getOptionPublic(event: ChatInputInteractionEvent) = event.getOption(OPTION_PUBLIC)
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asBoolean)
        .orElse(true).not()

    private fun getOptionFilter(event: ChatInputInteractionEvent) = event.getOption(OPTION_FILTER)
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asString)
        .orElse(".*").toRegex()

    private fun getOptionLength(event: ChatInputInteractionEvent) = event.getOption(OPTION_LENGTH)
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asLong)
        .orElse(50)


}
