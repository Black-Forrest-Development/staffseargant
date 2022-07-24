package de.sambalmueslie.discord.bot.staffsergeant.discord.db

import jakarta.persistence.*

@Entity(name = "GuildProcessorSettingsEntry")
@Table(name = "guild_processor_settings_entry")
data class GuildProcessorSettingsEntry(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    @Column()
    var guildId: Long = 0,
    @Column()
    var option: String = "",
    @Column()
    var enabled: Boolean = false
)
