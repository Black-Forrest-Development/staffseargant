package de.sambalmueslie.discord.bot.staffsergeant

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.info.*
import jakarta.inject.Singleton

@OpenAPIDefinition(
    info = Info(
        title = "staffsergeant",
        version = "\${api.version}",
        description = "\${openapi.description}",
    )
)
class StaffSergeantApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Micronaut.build()
                .args(*args)
                .packages("de.sambalmueslie.discord.bot.staffsergeant")
                .start()
        }
    }

    @Singleton
    internal class ObjectMapperBeanEventListener : BeanCreatedEventListener<ObjectMapper> {
        override fun onCreated(event: BeanCreatedEvent<ObjectMapper>): ObjectMapper {
            val mapper: ObjectMapper = event.bean
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            mapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            return mapper
        }
    }
}
