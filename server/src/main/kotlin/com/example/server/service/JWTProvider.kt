package com.example.server.service
/**
 * JWT provider interface provides the following methods:
 * - generateToken (both with and without subject version)
 * - verifyToken (both with and without subject version)
 */
interface JWTProvider {

    fun generateTokenNoSub(zoneId : Long): String

    fun verifyTokenNoSub(token: String, validityZone : Char) : Boolean

    fun generateToken(zoneId : Long): String

    fun verifyToken(token: String, validityZone : Char) : Boolean



}