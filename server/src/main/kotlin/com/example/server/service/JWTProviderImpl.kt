package com.example.server.service

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import java.util.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key

@Service
class JWTProviderImpl : JWTProvider, InitializingBean {

    @Value("\${app.config.hmac_key}")
    private lateinit var rawKey: String
    private lateinit var key: Key
    @Value("\${app.config.expiration_time}")
    private lateinit var expirationTime: String
    @Autowired
    private lateinit var ticketService: TicketService

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
           //mark ticket as invalid if is a valid one
           if (!ticketService.invalidTicket(jws.body.subject.split("_")[1].toLong()))
               return false
       }
       catch(e : JwtException){
           //exception triggered (due to inconsistency or expiration)
           return false
       }

        return true
    }

    override fun generateToken(validityZones: List<String>): String {
        //create a new ticket and add it into the ticket service
        return Jwts.builder() //factory builder
            .claim("vz", validityZones) //Validity zone private claim
            .setExpiration(Date(System.currentTimeMillis() + this.expirationTime.toLong())) //Exp reserved claim
            .signWith(this.key) //signing with (symmetric key)
            .setSubject(ticketService.getNewTicketId()) //Add subject ticket
            .compact() //Return compact representation
    }

    override fun verifyTokenNoSub(token: String, validityZone: Char): Boolean {
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

    override fun generateTokenNoSub(validityZones: List<String>): String {
        //create a new ticket and add it into the ticket service
        return Jwts.builder() //factory builder
            .claim("vz", validityZones) //Validity zone private claim
            .setExpiration(Date(System.currentTimeMillis() + this.expirationTime.toLong())) //Exp reserved claim
            .signWith(this.key) //signing with (symmetric key)
            .compact() //Return compact representation
    }

    override fun afterPropertiesSet() {
        key = Keys.hmacShaKeyFor(
                this.rawKey.toByteArray())
    }
}