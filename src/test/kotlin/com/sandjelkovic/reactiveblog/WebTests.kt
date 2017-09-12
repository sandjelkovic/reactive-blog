package com.sandjelkovic.reactiveblog

import org.apache.commons.lang3.RandomUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
        val blogsBefore = blogpostRepository.findAll().collectList().block()!!

        webClient.get().uri("/posts")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Blogpost::class.java).hasSize(10)
                .contains(*(blogsBefore.toTypedArray()))  // Usecase for Kotlin (*) expand operator!
    }

    @Test
    fun createNew() {
        val blogpostToSave = Blogpost(id = "27", title = "Title 27", content = "Content 27th")

        webClient.post().uri("/posts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(blogpostToSave), Blogpost::class.java)
                .exchange()
                .expectStatus().isCreated
                .expectHeader().valueEquals("Location", "/posts/${blogpostToSave.id}")

        val savedBlogpost = blogpostRepository.findById(blogpostToSave.id)
                .block()

        assertThat(savedBlogpost, equalTo(blogpostToSave))
    }

    @Test
    fun getById() {
        val blogsBefore = blogpostRepository.findAll().collectList().block()!!
        val existingBlog = blogsBefore[RandomUtils.nextInt(0, blogsBefore.size)]

        val responseBody = webClient.get().uri("/posts/${existingBlog.id}")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(Blogpost::class.java)
                .returnResult().responseBody

        assertThat(responseBody, notNullValue())
        assertThat(responseBody, samePropertyValuesAs(existingBlog))
    }

    @Test
    fun getByNonExistingId() {
        val blogsBefore = blogpostRepository.findAll().collectList().block()
        val nonExistingId = "99999"

        webClient.get().uri("/posts/$nonExistingId")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isNotFound
                .expectBody().isEmpty

    }

}
