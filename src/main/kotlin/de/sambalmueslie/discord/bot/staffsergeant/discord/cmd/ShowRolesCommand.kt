package de.sambalmueslie.discord.bot.staffsergeant.discord.cmd


import com.fasterxml.jackson.databind.ObjectMapper
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.interaction.InteractionCreateEvent
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import discord4j.core.`object`.command.ApplicationCommandInteractionOptionValue
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.Role
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
import kotlin.io.path.inputStream
import kotlin.io.path.writeText
import kotlin.system.measureTimeMillis


@Singleton
class ShowRolesCommand(
    private val mapper: ObjectMapper
) : Command {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ShowRolesCommand::class.java)
        private const val CMD = "show_roles"
        private const val OPTION_FILTER = "filter"
        private const val OPTION_PUBLIC = "public"
        private const val OPTION_ATTACHMENT = "attachment"
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
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(OPTION_ATTACHMENT)
                    .description("Add an JSON attachment")
                    .type(ApplicationCommandOption.Type.BOOLEAN.value)
                    .required(false).build()
            )
            .build()

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
                .then(handleShowRolesCommand(event))
                .subscribe()
        }
    }



    @Suppress("ReactiveStreamsUnusedPublisher")
    private suspend fun handleShowRolesCommand(event: ChatInputInteractionEvent): Mono<Message> {
        val guild = event.interaction.guild.awaitSingle() ?: return event.createFollowup("Cannot find guild")
        logger.info("Show roles for ${guild.name}")

        val members = getMembers(guild)
        logger.debug("Found ${members.size} members")
        val roles = getRoles(event, guild)
        logger.debug("Found ${roles.size} roles")
        val membersByRole = roles.map { it to getMemberForRole(it, members) }
            .filter { it.second.isNotEmpty() }

        val spec = InteractionFollowupCreateSpec.builder()
        var duration = measureTimeMillis { createContent(membersByRole, spec) }
        logger.debug("Create content within $duration ms.")

        val attachment = getOptionAttachment(event)
        logger.debug("Create attachment = $attachment")
        if (attachment) {
            duration = measureTimeMillis { addAttachment(membersByRole, spec) }
            logger.debug("Create attachment within $duration ms.")
        }

        return event.createFollowup(spec.build())

    }

    private fun createContent(
        membersByRole: List<Pair<Role, List<Member>>>,
        spec: InteractionFollowupCreateSpec.Builder
    ) {
        val description = StringBuilder()
        membersByRole.forEach { (role, members) ->
            description.append("[${role.mention}]").appendLine()
            members.forEach { m ->
                description.append(" - ").append(m.mention).appendLine()
            }
            description.appendLine()
        }
        spec.content(description.toString())
    }

    private suspend fun getMembers(guild: Guild): List<Member> {
        return withContext(Dispatchers.IO) {
            guild.members.toIterable().toList()
        }
    }

    private suspend fun addAttachment(
        membersByRole: List<Pair<Role, List<Member>>>,
        spec: InteractionFollowupCreateSpec.Builder
    ) {
        val output = membersByRole.map { (role, members) ->
            MembersByRole(role.name, members.map { it.displayName })
        }
        val file = withContext(Dispatchers.IO) {
            Files.createTempFile("member", "")
        }

        file.writeText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(output))
        spec.addFile("roles.json", file.inputStream())
    }

    private suspend fun getRoles(event: ChatInputInteractionEvent, guild: Guild): List<Role> {
        val filter = getOptionFilter(event)
        logger.debug("Build roles with filter '${filter.pattern}'")
        return withContext(Dispatchers.IO) {
            guild.roles.toIterable()
                .filter { it.name.matches(filter) }
                .sortedByDescending { it.rawPosition }
        }
    }
    private fun getOptionPublic(event: ChatInputInteractionEvent) = event.getOption(OPTION_PUBLIC)
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asBoolean)
        .orElse(true).not()
    private fun getOptionFilter(event: ChatInputInteractionEvent) = event.getOption(OPTION_FILTER)
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asString)
        .orElse(".*").toRegex()

    private fun getOptionAttachment(event: ChatInputInteractionEvent) = event.getOption(OPTION_FILTER)
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asBoolean)
        .orElse(true)

    private fun getMemberForRole(role: Role, members: Iterable<Member>): List<Member> {
        return members.filter { it.roleIds.contains(role.id) }.sortedBy { it.displayName }.toList()
    }
}

data class MembersByRole(
    val role: String,
    val members: List<String>
)
