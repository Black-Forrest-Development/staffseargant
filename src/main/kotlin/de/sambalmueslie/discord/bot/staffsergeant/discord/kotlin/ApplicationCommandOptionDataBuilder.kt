package de.sambalmueslie.discord.bot.staffsergeant.discord.kotlin

import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ImmutableApplicationCommandOptionData

class ApplicationCommandOptionDataBuilder(
    val builder: ImmutableApplicationCommandOptionData.Builder
) {

    inline fun type(type: () -> Int) {
        builder.type(type.invoke())
    }

    inline fun name(name: () -> String) {
        builder.name(name.invoke())
    }

    inline fun description(description: () -> String) {
        builder.description(description.invoke())
    }

    inline fun required(required: () -> Boolean) {
        builder.required(required.invoke())
    }

    inline fun choices(choices: () -> List<ApplicationCommandOptionChoiceData>) {
        builder.choices(choices.invoke())
    }


    inline fun options(options: () -> List<ApplicationCommandOptionData>) {
        builder.options(options.invoke())
    }

    inline fun autocomplete(autocomplete: () -> Boolean) {
        builder.autocomplete(autocomplete.invoke())
    }

    inline fun channelTypes(channelTypes: () -> List<Int>) {
        builder.channelTypes(channelTypes.invoke())
    }

    inline fun minValue(minValue: () -> Double) {
        builder.minValue(minValue.invoke())
    }

    inline fun maxValue(maxValue: () -> Double) {
        builder.maxValue(maxValue.invoke())
    }
    fun build(): ImmutableApplicationCommandOptionData = builder.build()
}


fun option(lambda: ApplicationCommandOptionDataBuilder.() -> Unit) =
    ApplicationCommandOptionDataBuilder(ApplicationCommandOptionData.builder())
        .apply(lambda)
        .build()
