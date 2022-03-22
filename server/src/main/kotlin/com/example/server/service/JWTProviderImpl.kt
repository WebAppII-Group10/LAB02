package com.example.server.service

import com.example.server.resources.SECRET
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.io.Encoders
import java.util.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JWTProviderImpl : JWTProvider {

    private val key = Keys.hmacShaKeyFor(
        Decoders.BASE64.decode(
            Encoders.BASE64.encode(SECRET.encodeToByteArray())))
    @Value("\${app.config.expiration_time}")
    private lateinit var expirationTime: String

    override fun verifyToken(token: String, validityZone: Char): Boolean {
       try{
           //Try to get token (automatic check on timestamp)
           val jws = Jwts.parserBuilder()
               .setSigningKey(this.key)
               .build()
               .parseClaimsJws(token)
           //Check for validity zone
           if(!jws.body["vz"].toString().filter{ it.isLetterOrDigit() }.contains(validityZone))
               return false
       }
       catch(e : JwtException){
           //exception triggered (due to inconsistency or expiration)
           return false
       }
        return true
    }

    override fun generateToken(validityZones: List<String>): String {
        println(this.expirationTime)
        return Jwts.builder() //factory builder
            .claim("vz", validityZones) //Validity zone private claim
            .setExpiration(Date(System.currentTimeMillis() + this.expirationTime.toLong())) //Exp reserved claim
            .signWith(this.key) //signing with (symmetric key)
            .compact() //Return compact representation
    }

}