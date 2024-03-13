package com.example.tubespbd.responses

data class LoginResponse(val token: String)

data class LoginRequest(val email: String, val password: String)