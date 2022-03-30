package com.example.server.controller

import com.example.server.entity.Ticket
import com.example.server.resources.*
import com.example.server.service.JWTProviderImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

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
        //Check if zone id is valid otherwise return bad request
        if(!VALID_ZONES.containsKey(zoneId))
            return ResponseEntity.badRequest().build()
        //create a map to match json object
        val responseMap = mutableMapOf<String, String>()
        //create the token
        responseMap["token"] = jwtProvider.generateToken(VALID_ZONES[zoneId]!!.split(" ").toList())
        //return the response with associated data
        return ResponseEntity.ok(responseMap)
    }

    @GetMapping(GENERATE_TICKET_NO_SUB)
    fun generateTicketNoSub(@RequestParam zoneId: Long): ResponseEntity<Any?> {
        //Check if zone id is valid otherwise return bad request
        if(!VALID_ZONES.containsKey(zoneId))
            return ResponseEntity.badRequest().build()
        //create a map to match json object
        val responseMap = mutableMapOf<String, String>()
        //create the token
        responseMap["token"] = jwtProvider.generateTokenNoSub(VALID_ZONES[zoneId]!!.split(" ").toList())
        //return the response with associated data
        return ResponseEntity.ok(responseMap)
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
    fun verifyTicket(@RequestBody ticket: Ticket): ResponseEntity<Any> {
        try{
            //verify the token
            if (jwtProvider.verifyToken(ticket.getToken(), ticket.getZone()))
                return ResponseEntity.ok().build()
            return ResponseEntity.status(403).build()

        }
        catch (e : Exception){
            return ResponseEntity.status(403).build()
        }
    }

    @PostMapping(VERIFY_TICKET_NO_SUB)
    fun verifyTicketNoSub(@RequestBody ticket: Ticket): ResponseEntity<Any> {
        try{
            //verify the token
            if (jwtProvider.verifyTokenNoSub(ticket.getToken(), ticket.getZone()))
                return ResponseEntity.ok().build()
            return ResponseEntity.status(403).build()

        }
        catch (e : Exception){
            return ResponseEntity.status(403).build()
        }
    }



}