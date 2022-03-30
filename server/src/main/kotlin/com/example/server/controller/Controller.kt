package com.example.server.controller

import com.example.server.entity.TicketSubmitted
import com.example.server.resources.*
import com.example.server.service.JWTProviderImpl
import io.jsonwebtoken.JwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ROOT_API)
class Controller {


    @Autowired
    private lateinit var jwtProvider: JWTProviderImpl


    /**
     * GENERATE TICKET
     * Generate a ticket according to the zones string passed as a parameter
     * If the String is invalid an error is returned
     * @return
     *      a response entity object
     *      200: OK - ticket is correctly generated
     *          payload is a json composed by the token: {"token": "aaa.bbb.ccc"}
     *      400: BAD REQUEST - an error occurs
     *
     */
    @GetMapping(GENERATE_TICKET)
    fun generateTicket(@RequestParam zoneId: Long): ResponseEntity<Any?> {
        try{
            val responseMap = mutableMapOf<String, String>()
            responseMap["token"] = jwtProvider.generateToken(zoneId);
            return ResponseEntity.ok(responseMap)
        }catch(e : Exception){
            return ResponseEntity.badRequest().build()
        } catch(e : JwtException) {
            return ResponseEntity.status(500).build()
        }
    }

    @GetMapping(GENERATE_TICKET_NO_SUB)
    fun generateTicketNoSub(@RequestParam zoneId: Long): ResponseEntity<Any?> {

        try{
            val responseMap = mutableMapOf<String, String>()
            responseMap["token"] = jwtProvider.generateTokenNoSub(zoneId);
            return ResponseEntity.ok(responseMap)
        }catch(e : Exception){
            return ResponseEntity.badRequest().build()
        } catch(e : JwtException) {
            return ResponseEntity.status(500).build()
        }
    }

    /**
     * VERIFY TICKET
     * Get a json object composed by zone and token
     * @return a response entity object
     *          200: OK - ticket is valid
     *          403: FORBIDDEN - ticket is not valid anymore
     *              - Ticket expiration
     *              - Invalid zone
     *              - Invalid request
     *
     */
    @PostMapping(VERIFY_TICKET)
    fun verifyTicket(@RequestBody ticketSubmitted: TicketSubmitted): ResponseEntity<Any> {
        try{
            //verify the token
            if (jwtProvider.verifyToken(ticketSubmitted.getToken(), ticketSubmitted.getZone()))
                return ResponseEntity.ok().build()
            return ResponseEntity.status(403).build()

        }
        catch (e : Exception){
            return ResponseEntity.status(403).build()
        }
    }

    @PostMapping(VERIFY_TICKET_NO_SUB)
    fun verifyTicketNoSub(@RequestBody ticketSubmitted: TicketSubmitted): ResponseEntity<Any> {
        try{
            //verify the token
            if (jwtProvider.verifyTokenNoSub(ticketSubmitted.getToken(), ticketSubmitted.getZone()))
                return ResponseEntity.ok().build()
            return ResponseEntity.status(403).build()

        }
        catch (e : Exception){
            return ResponseEntity.status(403).build()
        }
    }



}