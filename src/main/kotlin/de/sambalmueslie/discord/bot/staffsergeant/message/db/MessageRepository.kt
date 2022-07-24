//package de.sambalmueslie.discord.bot.staffsergeant.message.db
//
//import io.micronaut.data.annotation.Query
//import io.micronaut.data.annotation.Repository
//import io.micronaut.data.jdbc.annotation.JdbcRepository
//import io.micronaut.data.model.query.builder.sql.Dialect
//import io.micronaut.data.repository.PageableRepository
//
//@Repository
//@JdbcRepository(dialect = Dialect.POSTGRES)
//interface MessageRepository : PageableRepository<MessageData, Long> {
//    fun findByName(name: String): MessageData?
//
//
//    @Query("SELECT m.* from message m INNER JOIN message_role r ON m.id = r.message_id WHERE r.guild_id = :guildId and r.role = :role")
//    fun findByRole(guildId: Long, role: String): List<MessageData>
//
//}
