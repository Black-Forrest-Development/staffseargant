package de.sambalmueslie.discord.bot.staffsergeant.discord.cmd


import com.fasterxml.jackson.databind.ObjectMapper
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import discord4j.core.`object`.command.ApplicationCommandInteractionOptionValue
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.Role
import discord4j.core.spec.EmbedCreateFields
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.InteractionFollowupCreateSpec
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.RestClient
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.nio.file.Files
import java.time.Instant
import kotlin.io.path.inputStream
import kotlin.io.path.writeText


@Singleton
class ShowRolesCommand(
    private val mapper: ObjectMapper
) : Command {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ShowRolesCommand::class.java)
        private const val CMD = "show_roles"
        private const val OPTION_FILTER = "filter"
        private const val OPTION_PUBLIC = "public"
    }

    override val name: String
        get() = CMD

    override fun register(restClient: RestClient, applicationId: Long) {
        val request: ApplicationCommandRequest = ApplicationCommandRequest.builder()
            .name(CMD)
            .description("Prints the roles and their member")
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(OPTION_FILTER)
                    .description("A role name regex filter")
                    .type(ApplicationCommandOption.Type.STRING.value)
                    .required(false).build()
            )
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(OPTION_PUBLIC)
                    .description("Public or personal response (default=public)")
                    .type(ApplicationCommandOption.Type.BOOLEAN.value)
                    .required(false).build()
            )
            .build()

        restClient.applicationService.createGlobalApplicationCommand(applicationId, request).subscribe()
    }

    override fun matches(event: ChatInputInteractionEvent): Boolean {
        return event.commandName == CMD
    }

    override suspend fun process(event: ChatInputInteractionEvent) {
        val ephemeral = event.getOption(OPTION_PUBLIC)
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asBoolean)
            .orElse(false)

        event.deferReply()
            .withEphemeral(ephemeral)
            .then(handleShowRolesCommand(event))
            .subscribe()
    }

    @Suppress("ReactiveStreamsUnusedPublisher")
    private suspend fun handleShowRolesCommand(event: ChatInputInteractionEvent): Mono<Message> {
        val guild = event.interaction.guild.awaitSingle() ?: return event.createFollowup("Cannot find guild")

        val members = withContext(Dispatchers.IO) {
            guild.members.toIterable()
        }

        val filter = event.getOption(OPTION_FILTER)
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asString)
            .orElse(".*").toRegex()

        val roles = withContext(Dispatchers.IO) {
            guild.roles.toIterable()
        }.filter { it.name.matches(filter) }.associateBy { it.name }

        val membersByRole = roles.values.associateWith { getMemberForRole(it, members) }
            .filterValues { it.isNotEmpty() }

        val builder = EmbedCreateSpec.builder()
        builder.author(EmbedCreateFields.Author.of("Staffsergeant", null, null))
        builder.title("Current Roles with filter '${filter.pattern}'")

        val description = StringBuilder()

        membersByRole.entries.sortedByDescending { it.key.rawPosition }.forEach { (role, members) ->
            description.append(role.mention).appendLine()
            members.sortedBy { it.displayName }.forEach { m ->
                description.append(" - ").append(m.mention).appendLine()
            }
            description.appendLine()
        }
        builder.description(description.toString())
        builder.timestamp(Instant.now())

        val spec = InteractionFollowupCreateSpec.builder()
        spec.addEmbed(builder.build())

        val output = membersByRole.entries.sortedByDescending { it.key.rawPosition }.map { (role, members) ->
            MembersByRole(role.name, members.sortedBy { it.displayName }.map { it.displayName })
        }
        val file = withContext(Dispatchers.IO) {
            Files.createTempFile("member", "")
        }

        file.writeText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(output))
        spec.addFile("roles.json", file.inputStream())

        return event.createFollowup(spec.build())

    }

    private fun getMemberForRole(role: Role, members: Iterable<Member>): List<Member> {
        return members.filter { it.roleIds.contains(role.id) }.toList()
    }
}

data class MembersByRole(
    val role: String,
    val members: List<String>
)
