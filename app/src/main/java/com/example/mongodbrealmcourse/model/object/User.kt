package com.example.mongodbrealmcourse.model.`object`

import org.bson.codecs.pojo.annotations.BsonId

data class User(
    @BsonId
    val _id: String,
    val name: String,
    val email: String,
    val password: String,
    val avatar: String,
    val address: String,
    val time_create: String
    )
