package de.sambalmueslie.discord.bot.staffsergeant.discord.db


import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity(name = "GuildSettings")
@Table(name = "guild_settings")
data class GuildSettingsData(
    @Id
    var guildId: Long = 0,
)
