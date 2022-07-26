package de.sambalmueslie.discord.bot.staffsergeant.discord.evt


import discord4j.core.event.domain.guild.GuildCreateEvent
import discord4j.core.event.domain.guild.GuildDeleteEvent
import discord4j.core.event.domain.guild.GuildUpdateEvent
import discord4j.core.event.domain.guild.MemberUpdateEvent
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class EventRegistrar {


    companion object {
        val logger: Logger = LoggerFactory.getLogger(EventRegistrar::class.java)
    }


    suspend fun handleEvent(event: MemberUpdateEvent) {
//        TODO("Not yet implemented")
    }

    suspend fun handleEvent(event: GuildCreateEvent) {
//        TODO("Not yet implemented")
    }

    suspend fun handleEvent(event: GuildUpdateEvent) {
//        TODO("Not yet implemented")
    }

    suspend fun handleEvent(event: GuildDeleteEvent) {
//        TODO("Not yet implemented")
    }
}
