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
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.RestClient
import discord4j.rest.util.Color
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.lang.Integer.min
import java.nio.file.Files
import java.time.Instant
import kotlin.io.path.inputStream
import kotlin.io.path.writeText
import kotlin.system.measureTimeMillis


@Singleton
class ExportRolesCommand(
    private val mapper: ObjectMapper
) : Command {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ExportRolesCommand::class.java)
        private const val CMD = "export_roles"
        private const val OPTION_FILTER = "filter"
        private const val OPTION_PUBLIC = "public"
        private const val OPTION_ATTACHMENT = "attachment"
        private const val LIMIT_DESCRIPTION = 4096
        private const val LIMIT_MESSAGE = 2000
        private const val LIMIT_EMBEDS = 10
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
                .doOnError { error -> logger.error("Failed to respond", error) }
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
        val channel = event.interaction.channel.awaitSingle()

        var duration = measureTimeMillis { createContent(channel, membersByRole) }
        logger.debug("Create content within $duration ms.")

        var totalDuration = duration
        val attachment = getOptionAttachment(event)
        logger.debug("Create attachment = $attachment")
        if (attachment) {
            duration = measureTimeMillis { addAttachment(channel, membersByRole) }
            logger.debug("Create attachment within $duration ms.")
            totalDuration += duration
        }

        return event.createFollowup("Done within $totalDuration ms")

    }

    private suspend fun createContent(channel: MessageChannel, membersByRole: List<Pair<Role, List<Member>>>) {
        membersByRole.forEach { (role, members) -> createRoleContent(channel, role, members) }
    }

    private suspend fun createRoleContent(channel: MessageChannel, role: Role, members: List<Member>) {
        logger.info("Send role message for ${role.name} with ${members.size} members")

        val content = StringBuilder()

        var current = 10
        var length = 1
        while (current > members.size && length < 10) {
            current *= 10
            length++
        }

        members.forEachIndexed { index, member ->
            content.append(index.plus(1).toString().padStart(length, '0')).append(". ")
            content.append(member.displayName).appendLine()
        }


        val totalLength = content.length
        if (totalLength >= LIMIT_DESCRIPTION) {
            logger.debug("Send split message of $totalLength bytes")


            var index = 0
            var page = 1
            while (index < totalLength && page < 100) {
                val end = min(index + LIMIT_DESCRIPTION, content.length)
                val lastPage = end == content.length
                val part = if (!lastPage) content.substring(index, end).substringBeforeLast('\n') else content.substring(index, end)
                logger.debug("Send part $index with size ${part.length}")

                sendRoleMessage(channel, role, if (page == 1) members.size else -1, part.toString(), page)

                index += part.length
                page++
            }

        } else {
            logger.debug("Send complete message of $totalLength bytes length")
            sendRoleMessage(channel, role, members.size, content.toString())
        }

    }

    private fun sendRoleMessage(channel: MessageChannel, role: Role, size: Int, content: String, page: Int = -1) {
        val title = if (page >= 0) "${role.name} - Page $page" else role.name
        val embed = EmbedCreateSpec.builder()
            .color(Color.BISMARK)
            .title(title)
            .description(content)
            .timestamp(Instant.now())
        if (size >= 0) embed.addField("Members", "$size", false)
        channel.createMessage(embed.build()).doOnError { error -> logger.error("Failed to send message", error) }.subscribe()
    }

    private suspend fun getMembers(guild: Guild): List<Member> {
        return withContext(Dispatchers.IO) {
            guild.members.toIterable().toList()
        }
    }

    private suspend fun addAttachment(
        channel: MessageChannel,
        membersByRole: List<Pair<Role, List<Member>>>
    ) {
        val spec = MessageCreateSpec.builder()
        val output = membersByRole.map { (role, members) ->
            MembersByRole(role.name, members.size, members.map { it.displayName })
        }
        val file = withContext(Dispatchers.IO) {
            Files.createTempFile("member", "")
        }

        file.writeText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(output))
        spec.addFile("roles.json", file.inputStream())
        channel.createMessage(spec.build()).doOnError { error -> logger.error("Failed to send attachment", error) }.subscribe()
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

    private fun getOptionAttachment(event: ChatInputInteractionEvent) = event.getOption(OPTION_ATTACHMENT)
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asBoolean)
        .orElse(false)

    private fun getMemberForRole(role: Role, members: Iterable<Member>): List<Member> {
        return members.filter { it.roleIds.contains(role.id) }.sortedBy { it.displayName }.toList()
    }
}

data class MembersByRole(
    val role: String,
    val size: Int,
    val members: List<String>
)
