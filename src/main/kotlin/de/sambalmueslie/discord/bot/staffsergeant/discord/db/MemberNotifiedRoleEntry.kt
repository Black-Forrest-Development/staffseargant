package de.sambalmueslie.discord.bot.staffsergeant.discord.db

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity(name = "MemberNotifiedRoleEntry")
@Table(name = "member_notified_role_entry")
data class MemberNotifiedRoleEntry(
    @Id
    var id: String = "",
    @Column
    var memberId: Long = 0,
    @Column
    var role: String = ""
)
