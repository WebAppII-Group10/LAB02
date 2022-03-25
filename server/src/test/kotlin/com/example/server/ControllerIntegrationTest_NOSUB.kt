package com.example.server

import com.example.server.resources.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest
@AutoConfigureMockMvc
class ControllerIntegrationTest_NOSUB {

    //Mocked web client
    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun generateTicketTest() {
        //Mock up a web client: valid zoneId
        val value =  mvc.perform(
            MockMvcRequestBuilders.get("$ROOT_API$GENERATE_TICKET_NO_SUB?zoneId=1")
            //set up headers
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            //expected response status (it acts as an assertion)
            .andExpect(MockMvcResultMatchers.status().isOk)
            //get the request/response flow result (type is an MockMvcResult)
            .andReturn()
        val json = value.response.contentAsString
        //Content is not null
        Assertions.assertNotNull(json)
        Assertions.assertTrue(json.isNotEmpty())
        //response is a json string  which contains "{"token" : "aaa.bbb.ccc"}" -> check for "token" string
        Assertions.assertTrue(json.contains("token"))
    }

    @Test
    fun generateTicketTest_ErrorHandler1(){
        //Mock up a web client: invalid zoneId
        val value =  mvc.perform(
            MockMvcRequestBuilders.get("$ROOT_API$GENERATE_TICKET_NO_SUB?zoneId=4")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()
        val json = value.response.contentAsString
        Assertions.assertTrue(json.isEmpty())
    }

    @Test
    fun generateTicketTest_ErrorHandler2(){
        //Mock up a web client: no parameter
        val value =  mvc.perform(
            MockMvcRequestBuilders.get("$ROOT_API$GENERATE_TICKET_NO_SUB")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()
        val json = value.response.contentAsString
        Assertions.assertTrue(json.isEmpty())
    }

    @Test
    fun verifyTicketTest(){
        //Perform a valid token generation
        val tmp =  mvc.perform(
            MockMvcRequestBuilders.get("$ROOT_API$GENERATE_TICKET_NO_SUB?zoneId=1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val token = tmp.response.contentAsString.split(":")[1].split('"')[1]
        //Perform a verification for that token (just evaluate the status)
        mvc.perform(
            MockMvcRequestBuilders.post("$ROOT_API$VERIFY_TICKET_NO_SUB")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            //set up the json content (string format, pay attention to blank chars)
            .content("""{"token" : "$token", "zone" : "A"}"""))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
    }

    @Test
    fun verifyTicketTest_ErrorHandler1(){
        //Invalid Token
        mvc.perform(
            MockMvcRequestBuilders.post("$ROOT_API$VERIFY_TICKET_NO_SUB")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            //set up the json content (string format, pay attention to blank chars)
            .content("""{"token" : "AAA.BBB.CCC", "zone" : "A"}"""))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andReturn()
    }

    @Test
    fun verifyTicketTest_ErrorHandler2(){
        //Invalid json object
        mvc.perform(
            MockMvcRequestBuilders.post("$ROOT_API$VERIFY_TICKET_NO_SUB")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            //set up the json content (string format, pay attention to blank chars)
            .content("""{"key1" : "AAA.BBB.CCC", "key2" : "A"}"""))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()
    }

    @Test
    fun verifyTicketTest_ErrorHandler3(){
        //Perform a valid token generation
        val tmp =  mvc.perform(
            MockMvcRequestBuilders.get("$ROOT_API$GENERATE_TICKET_NO_SUB?zoneId=1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val token = tmp.response.contentAsString.split(":")[1].split('"')[1]
        //Perform a verification for that token (just evaluate the status)
        //Invalid zone ID
        mvc.perform(
            MockMvcRequestBuilders.post("$ROOT_API$VERIFY_TICKET")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            //set up the json content (string format, pay attention to blank chars)
            .content("""{"token" : "$token", "zone" : "Z"}"""))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andReturn()
    }

    @Test
    fun verifyTicketTest_ErrorHandler4(){
        //Perform a valid token generation
        val tmp =  mvc.perform(
            MockMvcRequestBuilders.get("$ROOT_API$GENERATE_TICKET_NO_SUB?zoneId=1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val token = tmp.response.contentAsString.split(":")[1].split('"')[1]
        //Perform a verification for that token (just evaluate the status)
        //expired token (wait 4 second -> expiration is set on 3 sec)
        Thread.sleep(4_000)
        mvc.perform(
            MockMvcRequestBuilders.post("$ROOT_API$VERIFY_TICKET")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            //set up the json content (string format, pay attention to blank chars)
            .content("""{"token" : "$token", "zone" : "A"}"""))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andReturn()
    }
}