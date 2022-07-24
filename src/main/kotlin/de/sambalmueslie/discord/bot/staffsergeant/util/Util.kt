package de.sambalmueslie.discord.bot.staffsergeant.util


import io.micronaut.data.repository.PageableRepository

fun <E, ID : Any> PageableRepository<E, ID>.findByIdOrNull(id: ID): E? = this.findById(id).orElseGet { null }
