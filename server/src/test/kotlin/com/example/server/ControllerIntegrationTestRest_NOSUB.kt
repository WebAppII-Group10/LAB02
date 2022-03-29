package com.example.server

import com.example.server.entity.Ticket
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
class ControllerIntegrationTestRest_NOSUB {

    @LocalServerPort
    protected var port : Int = 0

    @Autowired
    lateinit var restTemplate : TestRestTemplate

    @Autowired
    lateinit var jwtProvider : JWTProviderImpl


    @Test
    fun generateTicket(){
        val url : String = "http://localhost:$port" + ROOT_API + GENERATE_TICKET_NO_SUB + "?zoneId=1"
        val response = restTemplate.getForEntity(url, Ticket::class.java)
        assert(response.statusCode.is2xxSuccessful)
    }

    @Test
    fun generateTicket_ErrorHandler1(){
        val url : String = "http://localhost:$port" + ROOT_API + GENERATE_TICKET_NO_SUB + "?zoneId=4"
        val response = restTemplate.getForEntity(url, Ticket::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun generateTicket_ErrorHandler2(){
        val url : String = "http://localhost:$port" + ROOT_API + GENERATE_TICKET_NO_SUB
        Assertions.assertThrows(RestClientException::class.java){restTemplate.getForEntity(url, Ticket::class.java)}
    }

    @Test
    fun verifyTicket(){
        val token = jwtProvider.generateTokenNoSub(VALID_ZONES.get(1).toString().split(" "))
        val ticket = Ticket('A', token)
        val url : String = "http://localhost:$port" + ROOT_API + VERIFY_TICKET_NO_SUB
        val request = HttpEntity<Ticket>(ticket)
        val response = restTemplate.postForEntity<Unit>(url, request)
        assert(response.statusCode.is2xxSuccessful)
    }

    @Test
    fun verifyTicket_ErrorHandler1(){
        val ticket = Ticket('A', "AAA.BBB.CCC")
        val url : String = "http://localhost:$port" + ROOT_API + VERIFY_TICKET_NO_SUB
        val request = HttpEntity<Ticket>(ticket)
        val response = restTemplate.postForEntity<Unit>(url, request)
        assert(response.statusCode == HttpStatus.FORBIDDEN)
    }

    @Test
    fun verifyTicket_ErrorHandler2(){
        val url : String = "http://localhost:$port" + ROOT_API + VERIFY_TICKET_NO_SUB
        val request = HttpEntity<Any>("Wrong request")
        val response = restTemplate.postForEntity<Unit>(url, request)
        assert(response.statusCode == HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }
}