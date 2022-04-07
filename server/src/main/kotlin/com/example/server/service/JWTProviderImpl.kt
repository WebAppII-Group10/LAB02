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

/**
 *
 * Service provider for the JWT features to the controller
 *
 */
@Service
class JWTProviderImpl : JWTProvider, InitializingBean {

    //raw secret for key generation (should guarantee a minimum entropy level)
    @Value("\${app.config.hmac_key}")
    private lateinit var rawKey: String
    //key generated
    private lateinit var key: Key

    //expiration time for the ticket
    @Value("\${app.config.expiration_time}")
    private lateinit var expirationTime: String

    //autowire ticketService to store tickets references.
    @Autowired
    private lateinit var ticketService: TicketService

    /**
     *
     * Generate a new ticket (NoSub version)
     * generate a new ticket based on the zoneId which will composed the payload
     * set the expiration based on current system timestamp and the expiration time
     * then sign the token according to key and algo
     *  @param : ZoneId as Long (check resources/Constants.kt)
     *      must be checked if the zoneId is not present in the system an exception is raised
     *  @return: token as a string
     *  @throws: exception if token cannot be instanced or validity zone is not present in the system
     *
     */
    override fun generateTokenNoSub(zoneId: Long): String {
        try {
            //check if zoneId is present
            if (!VALID_ZONES.containsKey(zoneId)) {
                throw Exception()
            }
            //return the token as a string
            return Jwts.builder() //factory builder
                .claim("vz", VALID_ZONES[zoneId]!!.split(" ").toList()) //Validity zone private claim
                .setExpiration(Date(System.currentTimeMillis() + this.expirationTime.toLong())) //Exp reserved claim
                .signWith(this.key) //signing with (symmetric key)
                .compact()
        } catch (e: Exception) {
            //exception is thrown
            throw e
        }

    }

    /**
     *
     * Verify a ticket by means of token and validity zone (NoSub version)
     * extract a jwtClaim collector and check for consistency in the expiration time, and for JWT parameter (payload and signature)
     * according to algo and key involved.
     * Check if the validity zone is present in the one committed in the payload of the token
     * @param:
     *  token: String form of jwt
     *  validityZone: char which expresses the submission ticket station zone
     * @return: return true if everything is good otherwise
     *
     */
    override fun verifyTokenNoSub(token: String, validityZone: Char): Boolean {
        try {
            //Try to get token (automatic check on timestamp)
            val jws = Jwts.parserBuilder()
                .setSigningKey(this.key) //
                .build()
                .parseClaimsJws(token)
            //Check for validity zone
            if (!jws.body["vz"].toString().filter { it.isLetterOrDigit() }.contains(validityZone))
                return false
        } catch (e: JwtException) {
            //exception triggered (due to inconsistency or expiration)
            throw e
        }
        return true
    }

    /**
     *
     * Generate a new ticket (Standard version)
     * generate a new ticket based on the zoneId which will composed the payload
     * set the expiration based on current system timestamp and the expiration time
     * set the subject according to the UID returned by the TicketService
     * then sign the token according to key and algo
     * @param : ZoneId as Long (check resources/Constants.kt)
     *      must be checked if the zoneId is not present in the system an exception is raised
     * @return: token as a string
     * @throws: exception if token cannot be instanced or validity zone is not present in the system
     *
     */
    override fun generateToken(zoneId: Long): String {
        //create a new ticket and add it into the ticket service
        try {
            if (!VALID_ZONES.containsKey(zoneId)) {
                throw Exception()
            }
            return Jwts.builder() //factory builder
                .claim("vz", VALID_ZONES[zoneId]!!.split(" ").toList()) //Validity zone private claim
                .setExpiration(Date(System.currentTimeMillis() + this.expirationTime.toLong())) //Exp reserved claim
                .signWith(this.key) //signing with (symmetric key)
                .setSubject(ticketService.getNewTicketId()) //Add subject ticket
                .compact()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     *
     * Verify a ticket by means of token and validity zone (Standard version)
     * extract a jwtClaim collector and check for consistency in the expiration time, and for JWT parameter (payload and signature)
     * according to algo and key involved.
     * Check if the validity zone is present in the one committed in the payload of the token
     * Check also if the UID of the sub's claim is for an uncommitted ticket, then mark that ticket as committed
     * @param:
     *  token: String form of jwt
     *  validityZone: Char which expresses the submission ticket station zone
     * @return: return true if everything is good otherwise
     *
     */
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
            throw e
        }
        return true
    }

    /**
     *
     * Override function in order to process the key for the algo after the initialization of the component
     * otherwise raw key is not initialized
     *
     */
    override fun afterPropertiesSet() {
        key = Keys.hmacShaKeyFor(
            this.rawKey.toByteArray()
        )
    }
}