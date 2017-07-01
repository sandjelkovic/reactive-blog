package com.sandjelkovic.reactiveblog

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ReactiveBlogApplication {
    @Bean fun blogpostHandler(blogpostRepository: BlogpostRepository) = BlogpostHandler(blogpostRepository);
}

fun main(args: Array<String>) {
    SpringApplication.run(ReactiveBlogApplication::class.java, *args)
}
