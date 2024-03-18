package com.example.tubespbd.responses

data class LoginResponse(val token: String)

data class LoginRequest(val email: String, val password: String)

data class CheckResponse(val nim: String, val iat: String, val exp: String)

data class ItemResponse(val items: Items)

data class Items(val items: List<Bill>)

data class Bill(val name: String, val qty: Int, val price: Double)