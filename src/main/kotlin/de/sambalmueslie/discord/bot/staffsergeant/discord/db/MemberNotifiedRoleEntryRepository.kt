package de.sambalmueslie.discord.bot.staffsergeant.discord.db

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
interface MemberNotifiedRoleEntryRepository : CrudRepository<MemberNotifiedRoleEntry, String> {

    fun findByMemberId(memberId: Long): List<MemberNotifiedRoleEntry>
}
