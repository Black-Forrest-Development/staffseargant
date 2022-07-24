package de.sambalmueslie.discord.bot.staffsergeant.discord


import de.sambalmueslie.discord.bot.staffsergeant.config.AppConfig
import de.sambalmueslie.discord.bot.staffsergeant.discord.cmd.Command
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.guild.MemberUpdateEvent
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import jakarta.inject.Singleton
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Singleton
class DiscordBot(
    private val config: AppConfig,

    private val commands: List<Command>
) : ApplicationEventListener<ServerStartupEvent> {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DiscordBot::class.java)
    }


    override fun onApplicationEvent(event: ServerStartupEvent) {
        setupBot()
    }


    private fun setupBot() {
        val client = DiscordClient.create(config.token)

        client.gateway().setEnabledIntents(
            IntentSet.of(
                Intent.GUILD_MEMBERS, Intent.GUILDS, Intent.GUILD_MESSAGES, Intent.GUILD_MESSAGE_REACTIONS, Intent.DIRECT_MESSAGES, Intent.DIRECT_MESSAGE_REACTIONS
            )
        ).setInitialPresence { ClientPresence.online(ClientActivity.playing("Post Scriptum ;-)")) }.login().subscribe { handleLoggedIn(it) }
    }

    private fun handleLoggedIn(client: GatewayDiscordClient) {
        mono {
            client.on(MemberUpdateEvent::class.java).asFlow().collect { evt -> handleMemberUpdateEvent(evt) }
        }.subscribe()

        val restClient = client.restClient
        val applicationId = restClient.applicationId.block() ?: return logger.error("Not application id found")

        commands.forEach { it.register(restClient, applicationId) }

        mono {
            client.on(ChatInputInteractionEvent::class.java).asFlow().collect { evt -> handleChatInputInteractionEvent(evt) }
        }.subscribe()
    }

    private suspend fun handleChatInputInteractionEvent(event: ChatInputInteractionEvent) {
        val cmd = commands.firstOrNull { it.matches(event) }
        if (cmd == null) {
            event.reply("Unknown command ${event.commandName}")
        } else {
            cmd.process(event)
        }
    }


    private suspend fun handleMemberUpdateEvent(event: MemberUpdateEvent) {
        // TODO not implemented yet
    }


}
