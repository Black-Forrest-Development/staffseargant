package de.sambalmueslie.discord.bot.staffsergeant.discord.processor


import de.sambalmueslie.discord.bot.staffsergeant.discord.db.GuildReferenceData
import de.sambalmueslie.discord.bot.staffsergeant.discord.db.GuildReferenceRepository
import discord4j.core.event.domain.guild.GuildCreateEvent
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class RegisterBotProcessor(
    private val guildReferenceRepository: GuildReferenceRepository
) {


    companion object {
        val logger: Logger = LoggerFactory.getLogger(RegisterBotProcessor::class.java)
    }

    suspend fun handleEvent(event: GuildCreateEvent) {
        val guild = event.guild
        val existing = (withContext(Dispatchers.IO) {
            guildReferenceRepository.existsById(guild.id.asLong())
        })
        if (existing) return
        withContext(Dispatchers.IO) {
            guildReferenceRepository.save(GuildReferenceData(guild.id.asLong(), guild.name))
        }
    }
}
