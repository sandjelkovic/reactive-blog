package com.sandjelkovic.reactiveblog

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router


/**
 * @author sandjelkovic
 * @date 30.6.17.
 */
@Configuration
class Routes {
    @Bean
    fun apiRouter(blogpostHandler: BlogpostHandler) = router {
        ("/posts").nest {
            GET("/", f = { serverRequest -> blogpostHandler.getAll() })
            GET("/{id}", f = { serverRequest -> blogpostHandler.getById(serverRequest) })
            (accept(MediaType.APPLICATION_JSON_UTF8) and contentType(MediaType.APPLICATION_JSON_UTF8)).nest {
                POST("/", f = { serverRequest -> blogpostHandler.save(serverRequest) })
            }
        }
    }
}
