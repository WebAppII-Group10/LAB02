package com.example.server.integrationTest

import com.example.server.entity.TicketSubmitted
import com.example.server.resources.*
import com.example.server.service.JWTProviderImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClientException


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllerIntegrationTestRestNoSub {

    @LocalServerPort
    protected var port : Int = 0

    @Autowired
    lateinit var restTemplate : TestRestTemplate

    @Autowired
    lateinit var jwtProvider : JWTProviderImpl

    //Generate ticket with valid zone Id
    @Test
    fun generateTicket(){
        val url : String = "http://localhost:$port" + ROOT_API + GENERATE_TICKET_NO_SUB + "?zoneId=1"
        val response = restTemplate.getForEntity(url, TicketSubmitted::class.java)
        assert(response.statusCode.is2xxSuccessful)
    }

    //Generate ticket with invalid zoneId
    @Test
    fun generateTicket_ErrorHandler1(){
        val url = "http://localhost:$port$ROOT_API$GENERATE_TICKET_NO_SUB?zoneId=4"
        val response = restTemplate.getForEntity(url, TicketSubmitted::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    //generate ticket without parameter
    @Test
    fun generateTicket_ErrorHandler2(){
        val url : String = "http://localhost:$port" + ROOT_API + GENERATE_TICKET_NO_SUB
        Assertions.assertThrows(RestClientException::class.java){restTemplate.getForEntity(url, TicketSubmitted::class.java)}
    }

    //test verify ticket (with valid ticket)
    @Test
    fun verifyTicket(){
        //generate a valid ticket
        val token = jwtProvider.generateTokenNoSub(1)
        val ticketSubmitted = TicketSubmitted('A', token)
        //verify the ticket
        val url : String = "http://localhost:$port" + ROOT_API + VERIFY_TICKET_NO_SUB
        val request = HttpEntity<TicketSubmitted>(ticketSubmitted)
        val response = restTemplate.postForEntity<Unit>(url, request)
        assert(response.statusCode.is2xxSuccessful)
    }

    //verify invalid ticket
    @Test
    fun verifyTicket_ErrorHandler1(){
        val ticketSubmitted = TicketSubmitted('A', "AAA.BBB.CCC")
        val url : String = "http://localhost:$port" + ROOT_API + VERIFY_TICKET_NO_SUB
        val request = HttpEntity<TicketSubmitted>(ticketSubmitted)
        val response = restTemplate.postForEntity<Unit>(url, request)
        assert(response.statusCode == HttpStatus.FORBIDDEN)
    }

    //verify ticket not matching body
    @Test
    fun verifyTicket_ErrorHandler2(){
        val url : String = "http://localhost:$port" + ROOT_API + VERIFY_TICKET_NO_SUB
        val request = HttpEntity<Any>("Wrong request")
        val response = restTemplate.postForEntity<Unit>(url, request)
        assert(response.statusCode == HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }
}