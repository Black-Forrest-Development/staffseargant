package de.sambalmueslie.discord.bot.staffsergeant.discord.kotlin

import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.discordjson.json.ImmutableApplicationCommandRequest

class ApplicationCommandRequestBuilder(
    val builder: ImmutableApplicationCommandRequest.Builder
) {

    inline fun name(name: () -> String) {
        builder.name(name.invoke())
    }

    inline fun description(description: () -> String) {
        builder.description(description.invoke())
    }


    inline fun type(type: () -> Int) {
        builder.type(type.invoke())
    }

    inline fun defaultPermission(defaultPermission: () -> Boolean) {
        builder.defaultPermission(defaultPermission.invoke())
    }


    inline fun options(options: ApplicationCommandOptionDataBuilder.() -> Unit) {
        builder.addOption(ApplicationCommandOptionDataBuilder(ApplicationCommandOptionData.builder()).apply(options).build())
    }

    fun build(): ImmutableApplicationCommandRequest = builder.build()
}


fun applicationCommand(lambda: ApplicationCommandRequestBuilder.() -> Unit) =
    ApplicationCommandRequestBuilder(ApplicationCommandRequest.builder())
        .apply(lambda)
        .build()
