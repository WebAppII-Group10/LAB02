package com.example.server.unitTest

import com.example.server.entity.TicketSubmitted
import com.example.server.service.JWTProviderImpl
import io.jsonwebtoken.JwtException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * Unit test for JWTProviderImpl(NoSub version)
 *
 */
@SpringBootTest
class JWTProviderImplUnitTestNoSub() {

    @Autowired
    lateinit var jwtProvider : JWTProviderImpl

    //generate ticket correctly
    @Test
    fun generateToken() {
        Assertions.assertDoesNotThrow() {
            jwtProvider.generateTokenNoSub(1)
        }
    }

    //generate ticket with invalid zone
    @Test
    fun generateInvalidZonesToken() {
        Assertions.assertThrows(Exception::class.java) {
            jwtProvider.generateTokenNoSub(0)
        }
    }

    //generate ticket correctly
    @Test
    fun acceptValidToken() {
        val token = jwtProvider.generateTokenNoSub(1)
        val ticketSubmitted = TicketSubmitted('A', token)
        Assertions.assertDoesNotThrow() {
            jwtProvider.verifyTokenNoSub(ticketSubmitted.getToken(), ticketSubmitted.getZone())
        }
    }

    //reject wrong ticket
    @Test
    fun rejectInvalidToken() {
        Assertions.assertThrows(JwtException::class.java) {
            jwtProvider.verifyTokenNoSub("aaa.bbb.ccc", '1')
        }
    }

    //accept valid ticket
    @Test
    fun acceptValidTicket() {
        //generate token
        val token = jwtProvider.generateTokenNoSub(1)
        val ticketSubmitted = TicketSubmitted('A', token)
        //verify tioken
        Assertions.assertTrue() {
            jwtProvider.verifyTokenNoSub(ticketSubmitted.getToken(), ticketSubmitted.getZone())
        }
    }

    //accept multiple times the token
    @Test
    fun acceptSubmittedTicket() {
        val token = jwtProvider.generateTokenNoSub(1)
        val ticketSubmitted = TicketSubmitted('A', token)
        Assertions.assertTrue() {
            jwtProvider.verifyTokenNoSub(ticketSubmitted.getToken(), ticketSubmitted.getZone())
        }
        Assertions.assertTrue() {
            jwtProvider.verifyTokenNoSub(ticketSubmitted.getToken(), ticketSubmitted.getZone())
        }
    }

    //reject expired ticket (token is previously generated)
    @Test
    fun rejectExpiredTicket() {
        val expiredToken = "eyJhbGciOiJIUzM4NCJ9.eyJ2eiI6WyJBIiwiQiIsIkMiXSwiZXhwIjoxNjQ4NjU4NjQ1LCJzdWIiOiJUSUNLRVRfMSJ9.8KMfp6-R8pyGClg3X0DPVZHs2OFz99ykyqRhD0WlQo4oOX0NoZhPeJJjZLVrW_8g"
        Assertions.assertThrows(JwtException::class.java) {
            jwtProvider.verifyTokenNoSub(expiredToken, '1')
        }
    }

    //reject ticket (due to invalid zone)
    @Test
    fun rejectInvalidZoneTicket() {
        val token = jwtProvider.generateTokenNoSub(1)
        val ticketSubmitted = TicketSubmitted(' ', token)
        Assertions.assertFalse() {
            jwtProvider.verifyTokenNoSub(ticketSubmitted.getToken(), ticketSubmitted.getZone() )
        }
    }



}