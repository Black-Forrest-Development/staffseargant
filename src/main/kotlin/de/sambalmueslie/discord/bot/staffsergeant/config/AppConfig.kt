package de.sambalmueslie.discord.bot.staffsergeant.config


import io.micronaut.context.annotation.ConfigurationProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotBlank

@ConfigurationProperties("app")
class AppConfig {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(de.sambalmueslie.discord.bot.staffsergeant.config.AppConfig::class.java)
    }

    @NotBlank
    var token: String = ""
        set(value) {
            de.sambalmueslie.discord.bot.staffsergeant.config.AppConfig.Companion.logger.info("Set token to ${value.replace(".".toRegex(), "#")}")
            field = value
        }


}
