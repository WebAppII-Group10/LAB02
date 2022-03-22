package com.example.server.service

interface JWTProvider {

    fun verifyToken(token: String, validityZone : Char) : Boolean

    fun generateToken(validityZones: List<String>): String


}