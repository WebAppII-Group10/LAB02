package com.example.server.controller

import com.example.server.entity.Ticket
import com.example.server.resources.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ROOT_API)
class Controller {

    @GetMapping(HOME)
    fun getHome(): String {
        return "Hello Group10"
    }

    @PostMapping(VERIFY_TICKET)
    fun verifyTicket(@RequestBody ticket: Ticket): ResponseEntity<Any> {
        val jwtObj = JWTProviderImpl()
        if (jwtObj.verifyToken(ticket.getToken(), ticket.getZone()))
            return ResponseEntity.ok().build()
        return ResponseEntity.status(403).build()
    }

    @GetMapping(GENERATE_TICKET)
    fun generateTicket(): ResponseEntity<String> {
        val jwtObj = JWTProviderImpl()
        return ResponseEntity.ok(jwtObj.generateToken(arrayOf("A", "B")))
    }


}