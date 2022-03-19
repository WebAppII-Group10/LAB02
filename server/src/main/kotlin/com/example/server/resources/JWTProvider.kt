package com.example.server.resources

interface JWTProvider {

    fun verifyToken(token: String, validityZone : Char) : Boolean

    fun generateToken(validityZones: Array<String>): String


}