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

    fun getById(serverRequest: ServerRequest): Mono<ServerResponse> = ServerResponse
            .ok()
            .body(blogpostRepository.findById(serverRequest.pathVariable("id")), Blogpost::class.java)

    fun save(serverRequest: ServerRequest): Mono<ServerResponse> {
        return blogpostRepository.saveAll(serverRequest.bodyToMono(Blogpost::class.java))
                // take the first one (first and only saved one)
                .next()
                .flatMap { blogpost ->
                    ServerResponse
                            .created(createLocationURI(blogpost))
                            .build()
                }
    }

    private fun createLocationURI(blogpost: Blogpost) = URI.create("/posts/${blogpost.id}")
}
