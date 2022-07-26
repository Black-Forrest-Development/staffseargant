package de.sambalmueslie.discord.bot.staffsergeant.discord.processor


import de.sambalmueslie.discord.bot.staffsergeant.discord.db.MemberNotifiedRoleEntryRepository
import jakarta.inject.Singleton

@Singleton
class NotifyOnAssignedRoleProcessor(
    private val repository: MemberNotifiedRoleEntryRepository,
) {

}
//    private val messageService: MessageService,
//    private val repository: MemberNotifiedRoleEntryRepository,
//) {
//
//    companion object {
//        val logger: Logger = LoggerFactory.getLogger(NotifyOnAssignedRoleProcessor::class.java)
//    }
//
//    private val ve = VelocityEngine()
//
//    init {
//        ve.init()
//    }
//
//    suspend fun handleMemberUpdateEvent(event: MemberUpdateEvent){
//        val member = event.member.awaitSingleOrNull() ?: return DiscordBot.logger.error("Cannot get member from event")
//        val channel = member.privateChannel.awaitSingle()
//        val roles = member.roles.toIterable().associateBy { it.name }
//        DiscordBot.logger.info("Found roles for member ${roles.keys}")
//
//        val memberId = member.id.asLong()
//        val entries = repository.findByMemberId(memberId).associateBy { it.role }
//
//        roles.values.forEach { updateActiveRoles(it, entries, channel, member) }
//        cleanupRemovedRoles(entries, roles)
//    }
//
//
//    private suspend fun updateActiveRoles(
//        role: Role, entries: Map<String, MemberNotifiedRoleEntry>, channel: PrivateChannel, member: Member
//    ) {
//        val message = messageService.getMessageForRole(role.guildId.asLong(), role.name)
//        val entry = entries[role.name]
//        if (message == null && entry == null) return
//        if (message == null && entry != null) {
//            withContext(Dispatchers.IO) {
//                repository.delete(entry)
//            }
//        }
//        if (message != null && entry == null) {
//            channel.createMessage(resolve(member, role, message)).awaitSingle()
//            val memberId = member.id.asLong()
//            withContext(Dispatchers.IO) {
//                repository.save(MemberNotifiedRoleEntry("$memberId#${role.name}", memberId, role.name))
//            }
//        }
//    }
//
//    private fun resolve(member: Member, role: Role, message: Message): String {
//        val context = VelocityContext(
//            mapOf(
//                Pair("member", member), Pair("role", role)
//            )
//        )
//        val writer = StringWriter()
//        ve.evaluate(context, writer, message.name, message.text)
//        return writer.toString()
//    }
//
//    private suspend fun cleanupRemovedRoles(
//        entries: Map<String, MemberNotifiedRoleEntry>, roles: Map<String, Role>
//    ) {
//        val removed = entries.filter { !roles.containsKey(it.key) }.map { it.value }
//        if (removed.isNotEmpty()) withContext(Dispatchers.IO) {
//            repository.deleteAll(removed)
//        }
//    }
//}
