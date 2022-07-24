package de.sambalmueslie.discord.bot.staffsergeant.discord.db


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity(name = "GuildReference")
@Table(name = "guild_reference")
data class GuildReferenceData(
    @Id
    var guildId: Long = 0,
    @Column()
    var name: String = ""
)
