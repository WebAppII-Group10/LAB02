package com.example.server

import com.example.server.service.JWTProviderImpl
import com.example.server.service.TicketService
import io.jsonwebtoken.JwtException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.xml.bind.ValidationException


@SpringBootTest
class UnitTests() {

    @Autowired
    lateinit var jwtProvider : JWTProviderImpl

    @Test
    fun rejectInvalidJWT() {
        Assertions.assertThrows(JwtException::class.java) {
            jwtProvider.verifyToken("aaa.bbb.ccc", '1')
           // ticketingService.validateTicket("1", "aaa.bbb.ccc")
        }
    }


}