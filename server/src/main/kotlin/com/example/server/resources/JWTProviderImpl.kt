package com.example.server.resources

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JWTProviderImpl : JWTProvider {

    private val algo = Algorithm.HMAC256(SECRET)
    private val expirationTime = EXPIRATION_TIME


    override fun verifyToken(token: String, validityZone: Char): Boolean {
        val verifier = JWT.require(this.algo).build()
        try {
            val decodedToken = verifier.verify(token)
            if (!decodedToken.getClaim("vz").toString().filter { it.isLetterOrDigit() }.contains(validityZone.toString()))
                return false
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun generateToken(validityZones: Array<String>): String {
        //val payloadMap = mapOf<String, Any>("vz" to validityZones, "exp" to (LocalDateTime.now().plus(this.expirationTime, ChronoUnit.SECONDS)))
        //return JWT.create().withPayload(payloadMap).sign(this.algo)
        return JWT.create().withArrayClaim("vz", validityZones).withExpiresAt(Date(System.currentTimeMillis() + this.expirationTime))
                .sign(this.algo)
    }
}