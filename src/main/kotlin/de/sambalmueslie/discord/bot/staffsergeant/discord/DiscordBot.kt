package de.sambalmueslie.discord.bot.staffsergeant.discord


import de.sambalmueslie.discord.bot.staffsergeant.config.AppConfig
import de.sambalmueslie.discord.bot.staffsergeant.discord.cmd.CommandService
import de.sambalmueslie.discord.bot.staffsergeant.discord.evt.EventService
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Singleton
class DiscordBot(
    private val config: AppConfig,
    private val commandService: CommandService,
    private val eventService: EventService
) : ApplicationEventListener<ServerStartupEvent> {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DiscordBot::class.java)
    }


    override fun onApplicationEvent(event: ServerStartupEvent) {
        setupBot()
    }


    private fun setupBot() {
        val client = DiscordClient.create(config.token)

        client.gateway()
            .setEnabledIntents(getIntends())
            .setInitialPresence { getInitialPresence() }
            .login()
            .subscribe { handleLoggedIn(it) }
    }

    private fun getInitialPresence(): ClientPresence {
        return ClientPresence.online(ClientActivity.playing("Post Scriptum ;-)"))
    }

    private fun getIntends(): IntentSet {
        return IntentSet.of(
            Intent.GUILD_MEMBERS,
            Intent.GUILDS,
            Intent.GUILD_MESSAGES,
            Intent.GUILD_MESSAGE_REACTIONS,
            Intent.DIRECT_MESSAGES,
            Intent.DIRECT_MESSAGE_REACTIONS
        )
    }

    private fun handleLoggedIn(client: GatewayDiscordClient) {
        eventService.register(client)
        commandService.register(client)
    }


    private suspend fun handleMemberUpdateEvent(event: MemberUpdateEvent) {
        // TODO not implemented yet
    }


}
