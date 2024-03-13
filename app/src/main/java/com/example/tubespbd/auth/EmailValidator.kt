package com.example.tubespbd.auth

object EmailValidator {
    private val EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@std\\.stei\\.itb\\.ac\\.id$".toRegex()
    fun isValidEmail(email: String): Boolean {
        return EMAIL_PATTERN.matches(email)
    }
}