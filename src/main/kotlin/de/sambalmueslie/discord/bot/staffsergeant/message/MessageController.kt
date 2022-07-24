//package de.sambalmueslie.discord.bot.staffsergeant.message
//
//import de.sambalmueslie.discord.bot.staffsergeant.message.api.Message
//import de.sambalmueslie.discord.bot.staffsergeant.message.api.MessageChangeRequest
//import io.micronaut.data.model.Page
//import io.micronaut.data.model.Pageable
//import io.micronaut.http.annotation.*
//import io.micronaut.security.authentication.Authentication
//import io.swagger.v3.oas.annotations.tags.Tag
//
//@Controller("/api/message")
//@Tag(name = "Message API")
//class MessageController(private val service: MessageService) {
//
//    @Get("/{messageId}")
//    fun get(authentication: Authentication, @PathVariable messageId: Long): Message? = service.get(authentication, messageId)
//
//    @Get()
//    fun getAll(authentication: Authentication, pageable: Pageable): Page<Message> = service.getAll(authentication, pageable)
//
//    @Post()
//    fun create(authentication: Authentication, @Body request: MessageChangeRequest) = service.create(authentication, request)
//
//    @Put("/{messageId}")
//    fun update(authentication: Authentication, @PathVariable messageId: Long, @Body request: MessageChangeRequest) = service.update(authentication, messageId, request)
//
//    @Delete("/{messageId}")
//    fun delete(authentication: Authentication, @PathVariable messageId: Long) = service.delete(authentication, messageId)
//}
