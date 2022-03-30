package com.example.server.service

interface JWTProvider {

    fun generateTokenNoSub(zoneId : Long): String

    fun verifyTokenNoSub(token: String, validityZone : Char) : Boolean

    fun generateToken(zoneId : Long): String

    fun verifyToken(token: String, validityZone : Char) : Boolean



}