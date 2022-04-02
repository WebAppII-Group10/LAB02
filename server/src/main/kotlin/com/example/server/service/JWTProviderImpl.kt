package com.example.server.service

import com.example.server.resources.VALID_ZONES
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

    override fun generateTokenNoSub(zoneId: Long): String {

        //create a new ticket and add it into the ticket service
        try {
            if (!VALID_ZONES.containsKey(zoneId)) {
                throw Exception();
            }
            return Jwts.builder() //factory builder
                .claim("vz", VALID_ZONES[zoneId]!!.split(" ").toList()) //Validity zone private claim
                .setExpiration(Date(System.currentTimeMillis() + this.expirationTime.toLong())) //Exp reserved claim
                .signWith(this.key) //signing with (symmetric key)
                .compact()
        } catch (e: Exception) {
            throw e;
        } catch (e: JwtException) {
            throw e;
        }

    }

    override fun verifyTokenNoSub(token: String, validityZone: Char): Boolean {
        try {
            //Try to get token (automatic check on timestamp)
            val jws = Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
            //Check for validity zone
            if (!jws.body["vz"].toString().filter { it.isLetterOrDigit() }.contains(validityZone))
                return false
        } catch (e: JwtException) {
            //exception triggered (due to inconsistency or expiration)
            return false
        }

        return true
    }

    override fun generateToken(zoneId: Long): String {
        //create a new ticket and add it into the ticket service
        try {
            if (!VALID_ZONES.containsKey(zoneId)) {
                throw Exception();
            }
            return Jwts.builder() //factory builder
                .claim("vz", VALID_ZONES[zoneId]!!.split(" ").toList()) //Validity zone private claim
                .setExpiration(Date(System.currentTimeMillis() + this.expirationTime.toLong())) //Exp reserved claim
                .signWith(this.key) //signing with (symmetric key)
                .setSubject(ticketService.getNewTicketId()) //Add subject ticket
                .compact()
        } catch (e: Exception) {
            throw e;
        } catch (e: JwtException) {
            throw e;
        }

        //Return compact representation
    }

    override fun verifyToken(token: String, validityZone: Char): Boolean {
        try {
            //Try to get token (automatic check on timestamp)
            val jws = Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
            //Check for validity zone
            if (!jws.body["vz"].toString().filter { it.isLetterOrDigit() }.contains(validityZone))
                return false
            //mark ticket as invalid if is a valid one
            if (!ticketService.invalidTicket(jws.body.subject.split("_")[1].toLong()))
                return false
        } catch (e: JwtException) {
            //exception triggered (due to inconsistency or expiration)
            throw e;
        }

        return true
    }

    override fun afterPropertiesSet() {
        key = Keys.hmacShaKeyFor(
            this.rawKey.toByteArray()
        )
    }
}