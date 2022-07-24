package de.sambalmueslie.discord.bot.staffsergeant.message.db

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Suppress("JpaMissingIdInspection")
@Entity(name = "MessageRole")
@Table(name = "message_role")
data class MessageRoleRelation(
    var messageId: Long = 0,
    var role: String = "",
    var guildId: Long = 0
)
