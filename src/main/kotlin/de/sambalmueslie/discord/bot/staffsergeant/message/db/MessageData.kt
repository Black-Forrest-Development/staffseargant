package de.sambalmueslie.discord.bot.staffsergeant.message.db

import de.sambalmueslie.discord.bot.staffsergeant.message.api.Message
import de.sambalmueslie.discord.bot.staffsergeant.message.api.MessageChangeRequest
import jakarta.persistence.*

@Entity(name = "Message")
@Table(name = "message")
data class MessageData(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    @Column()
    var name: String = "",
    @Column()
    var text: String = ""
) {

    companion object {
        fun create(request: MessageChangeRequest): MessageData {
            return MessageData(0, request.name, request.text)
        }
    }

    fun update(request: MessageChangeRequest): MessageData {
        name = request.name
        text = request.text
        return this
    }

    fun convert() = Message(id, name, text)
}
