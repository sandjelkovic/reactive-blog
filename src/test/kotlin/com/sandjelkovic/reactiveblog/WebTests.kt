package com.sandjelkovic.reactiveblog

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebTests {
    // Can't use Constructor injection.
    // java.lang.Exception: Test class should have exactly one public zero-argument constructor

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var blogpostRepository: BlogpostRepository

    @Before
    fun before() {
        val blogList = (1..10)
                .map { Blogpost(id = it.toString(), title = "Blog $it.", content = "Blog $it content.") }

        blogpostRepository.saveAll(Flux.fromIterable(blogList)).blockLast()
    }

    @Test
    fun getAll() {
        val blogsBefore = blogpostRepository.findAll().collectList().block()
        println("BlogBefore size: ${blogsBefore.size}")
        webClient.get().uri("/posts")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Blogpost::class.java).hasSize(10)
                .contains(*(blogsBefore.toTypedArray()))  // Usecase for Kotlin (*) expand operator!
    }
}
