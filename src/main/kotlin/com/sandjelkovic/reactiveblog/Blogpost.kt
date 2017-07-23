package com.sandjelkovic.reactiveblog

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

/**
 * @author sandjelkovic
 * @date 1.7.17.
 */
@Document
data class Blogpost(@Id val id: String,
                    val title: String,
                    val content: String)

@Repository
interface BlogpostRepository : ReactiveMongoRepository<Blogpost, Long>
