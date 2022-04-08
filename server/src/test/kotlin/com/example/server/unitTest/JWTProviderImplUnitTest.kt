package com.example.server.unitTest

import com.example.server.entity.TicketSubmitted
import com.example.server.service.JWTProviderImpl
import io.jsonwebtoken.JwtException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * Unit test for JWTProviderImpl(Standard version)
 */
@SpringBootTest
class JWTProviderImplUnitTest() {

    @Autowired
    lateinit var jwtProvider : JWTProviderImpl

    //generate ticket correctly
    @Test
    fun generateToken() {
        Assertions.assertDoesNotThrow() {
            jwtProvider.generateToken(1)
        }
    }

    //generate ticket with invalid zone
    @Test
    fun generateInvalidZonesToken() {
        Assertions.assertThrows(Exception::class.java) {
            //jwtProvider.generateToken("invalidZone")
            jwtProvider.generateToken(0)
        }
    }

    //reject wrong ticket
    @Test
    fun acceptValidToken() {
        val token = jwtProvider.generateToken(1)
        val ticketSubmitted = TicketSubmitted('A', token)
        Assertions.assertDoesNotThrow() {
            jwtProvider.verifyToken(ticketSubmitted.getToken(), ticketSubmitted.getZone())
        }
    }

    @Test
    fun rejectInvalidToken() {
        Assertions.assertThrows(JwtException::class.java) {
            jwtProvider.verifyToken("aaa.bbb.ccc", '1')
        }
    }
    //accept single time the a valid ticket
    @Test
    fun acceptValidTicket() {
        val token = jwtProvider.generateToken(1)
        val ticketSubmitted = TicketSubmitted('A', token)
        Assertions.assertTrue() {
            jwtProvider.verifyToken(ticketSubmitted.getToken(), ticketSubmitted.getZone())
        }
    }

    //reject expired ticket (token is previously generated)
    @Test
    fun rejectExpiredTicket() {
        val expiredToken = "eyJhbGciOiJIUzM4NCJ9.eyJ2eiI6WyJBIiwiQiIsIkMiXSwiZXhwIjoxNjQ4NjU4NjQ1LCJzdWIiOiJUSUNLRVRfMSJ9.8KMfp6-R8pyGClg3X0DPVZHs2OFz99ykyqRhD0WlQo4oOX0NoZhPeJJjZLVrW_8g"
        Assertions.assertThrows(JwtException::class.java) {
            jwtProvider.verifyToken(expiredToken, '1')
        }
    }

    //reject ticket (due to invalid zone)
    @Test
    fun rejectInvalidZoneTicket() {
        val token = jwtProvider.generateToken(1)
        val ticketSubmitted = TicketSubmitted(' ', token)
        Assertions.assertFalse() {
            jwtProvider.verifyToken(ticketSubmitted.getToken(), ticketSubmitted.getZone() )
        }
    }

    //reject already used ticket
    @Test
    fun rejectSubmittedTicket() {
        //generate token
        val token = jwtProvider.generateToken(1)
        val ticketSubmitted = TicketSubmitted('A', token)
        //valid ticket: correctly committed
        Assertions.assertTrue() {
            jwtProvider.verifyToken(ticketSubmitted.getToken(), ticketSubmitted.getZone())
        }
        //valid again: not permitted
        Assertions.assertFalse() {
            jwtProvider.verifyToken(ticketSubmitted.getToken(), ticketSubmitted.getZone())
        }
    }

}