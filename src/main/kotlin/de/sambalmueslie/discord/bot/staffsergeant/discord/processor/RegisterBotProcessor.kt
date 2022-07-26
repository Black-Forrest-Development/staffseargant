package de.sambalmueslie.discord.bot.staffsergeant.discord.processor


import de.sambalmueslie.discord.bot.staffsergeant.discord.db.GuildProcessorSettingsEntryRepository
import de.sambalmueslie.discord.bot.staffsergeant.discord.db.GuildReferenceData
import de.sambalmueslie.discord.bot.staffsergeant.discord.db.GuildReferenceRepository
import discord4j.core.event.domain.guild.GuildCreateEvent
import discord4j.core.`object`.entity.Guild
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class RegisterBotProcessor(
    private val guildReferenceRepository: GuildReferenceRepository,
    private val settingsEntryRepository: GuildProcessorSettingsEntryRepository
) {


    companion object {
        val logger: Logger = LoggerFactory.getLogger(RegisterBotProcessor::class.java)
    }

    suspend fun handleEvent(event: GuildCreateEvent) {
        val guild = event.guild
        updateGuildReference(guild)
        updateProcessorSettings(guild)
    }


    private suspend fun updateGuildReference(guild: Guild) {
        val existing = (withContext(Dispatchers.IO) {
            guildReferenceRepository.existsById(guild.id.asLong())
        })
        if (existing) return
        withContext(Dispatchers.IO) {
            guildReferenceRepository.save(GuildReferenceData(guild.id.asLong(), guild.name))
        }
    }


    private suspend fun updateProcessorSettings(guild: Guild) {
        val existing = settingsEntryRepository.findByGuildId(guild.id.asLong())
        if(existing.isEmpty()){

        } else {

        }
    }
}
