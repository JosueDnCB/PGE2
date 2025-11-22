package com.example.pge.models

data class loginApi (
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val user: User?
)

data class User(
    val id: Int,
    val name: String
)

