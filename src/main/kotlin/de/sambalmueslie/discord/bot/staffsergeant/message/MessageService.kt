//package de.sambalmueslie.discord.bot.staffsergeant.message
//
//
//import com.nimbusds.jose.shaded.json.JSONArray
//import de.sambalmueslie.discord.bot.staffsergeant.error.InvalidRequestException
//import de.sambalmueslie.discord.bot.staffsergeant.error.NotAuthorizedException
//import de.sambalmueslie.discord.bot.staffsergeant.message.api.Message
//import de.sambalmueslie.discord.bot.staffsergeant.message.api.MessageChangeRequest
//import de.sambalmueslie.discord.bot.staffsergeant.message.db.MessageData
//import de.sambalmueslie.discord.bot.staffsergeant.message.db.MessageRepository
//import de.sambalmueslie.discord.bot.staffsergeant.util.findByIdOrNull
//import io.micronaut.data.model.Page
//import io.micronaut.data.model.Pageable
//import io.micronaut.security.authentication.Authentication
//import jakarta.inject.Singleton
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//
//@Singleton
//class MessageService(
//    private val repository: MessageRepository
//) {
//
//
//    companion object {
//        val logger: Logger = LoggerFactory.getLogger(MessageService::class.java)
//        private const val READ_ROLE = "sf.message.read"
//        private const val WRITE_ROLE = "sf.message.write"
//    }
//
//    fun get(auth: Authentication, messageId: Long): Message? {
//        isReadAuthorized(auth)
//        return repository.findByIdOrNull(messageId)?.convert()
//    }
//
//    private fun isReadAuthorized(auth: Authentication) {
//        val permissions = auth.attributes["permissions"] as JSONArray
//        if (permissions.contains(READ_ROLE)) return
//        throw NotAuthorizedException(auth, "Message API", READ_ROLE, "User has not the required role")
//    }
//
//
//    fun getAll(auth: Authentication, pageable: Pageable): Page<Message> {
//        isReadAuthorized(auth)
//        return repository.findAll(pageable).map { it.convert() }
//    }
//
//    fun create(auth: Authentication, request: MessageChangeRequest): Message {
//        isWriteAuthorized(auth)
//        logger.debug("[${auth.name}] CREATE MESSAGE - $request")
//        return create(request)
//
//    }
//
//    private fun create(request: MessageChangeRequest): Message {
//        isRequestValid(request)
//
//        val existing = repository.findByName(request.name)
//        return if (existing != null) {
//            update(existing, request)
//        } else {
//            return repository.save(MessageData.create(request)).convert()
//        }
//    }
//
//    fun update(auth: Authentication, messageId: Long, request: MessageChangeRequest): Message {
//        isWriteAuthorized(auth)
//        logger.debug("[${auth.name}] UPDATE MESSAGE ($messageId) - $request")
//        val data = repository.findByIdOrNull(messageId) ?: return create(request)
//        return update(data, request)
//    }
//
//    private fun update(data: MessageData, request: MessageChangeRequest): Message {
//        isRequestValid(request)
//        return repository.update(data.update(request)).convert()
//    }
//
//    fun delete(auth: Authentication, messageId: Long) {
//        isWriteAuthorized(auth)
//        logger.debug("[${auth.name}] DELETE MESSAGE ($messageId)")
//        repository.deleteById(messageId)
//    }
//
//    private fun isWriteAuthorized(auth: Authentication) {
//        val permissions = auth.attributes["permissions"] as JSONArray
//        if (permissions.contains(WRITE_ROLE)) return
//        throw NotAuthorizedException(auth, "Message API", WRITE_ROLE, "User has not the required role")
//    }
//
//    private fun isRequestValid(request: MessageChangeRequest) {
//        if (request.name.isBlank()) throw InvalidRequestException("Name must not blank")
//    }
//
//    fun getMessageForRole(guildId: Long, name: String): Message? {
//        return repository.findByRole(guildId, name).firstOrNull()?.convert()
//    }
//
//}
