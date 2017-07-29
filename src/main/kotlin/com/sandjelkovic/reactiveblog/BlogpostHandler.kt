package com.sandjelkovic.reactiveblog

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

/**
 * @author sandjelkovic
 * @date 30.6.17.
 */
class BlogpostHandler(val blogpostRepository: BlogpostRepository) {
    fun getAll(): Mono<ServerResponse> = ServerResponse
            .ok()
            .body(blogpostRepository.findAll(), Blogpost::class.java)

    fun getById(serverRequest: ServerRequest): Mono<ServerResponse> =
            blogpostRepository.findById(serverRequest.pathVariable(PATH_VARIABLE_ID))
                    .flatMap { ServerResponse.ok().body(Mono.just(it), Blogpost::class.java) }
                    .switchIfEmpty(ServerResponse.notFound().build())

    fun save(serverRequest: ServerRequest): Mono<ServerResponse> {
        return serverRequest.bodyToMono(Blogpost::class.java)
                .flatMap { blogpostRepository.save(it) }
                .flatMap { blogpost -> ServerResponse.created(createLocationUrl(blogpost)).build() }
    }

    private fun createLocationUrl(blogpost: Blogpost) = URI.create("/posts/${blogpost.id}")
}

const val PATH_VARIABLE_ID = "id"
