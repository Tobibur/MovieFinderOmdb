package com.example.tobibur.moviefinderomdb.model

class ApiResponse {

    var posts: Movie? = null
    var error: Throwable? = null

    constructor(posts: Movie) {
        this.posts = posts
        this.error = null
    }

    constructor(error: Throwable) {
        this.error = error
        this.posts = null
    }
}