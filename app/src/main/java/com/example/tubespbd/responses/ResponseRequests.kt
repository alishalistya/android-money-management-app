package com.example.tubespbd.responses

data class LoginResponse(val token: String)

data class LoginRequest(val email: String, val password: String)

data class CheckResponse(val nim: String, val iat: String, val exp: String)