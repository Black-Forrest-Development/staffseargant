package de.sambalmueslie.discord.bot.staffsergeant.error


import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Produces
@Singleton
@Requires(classes = [NotAuthorizedException::class, ExceptionHandler::class])
class NotAuthorizedExceptionHandler : ExceptionHandler<NotAuthorizedException, HttpResponse<*>> {

    override fun handle(request: HttpRequest<*>, exception: NotAuthorizedException): HttpResponse<*> {
        return HttpResponse.unauthorized<String>().body(exception.error())
    }

}
