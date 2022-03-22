package com.example.server

import com.example.server.resources.GENERATE_TICKET
import com.example.server.resources.ROOT_API
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
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
class ServerApplicationTests {

    @Autowired
    private lateinit var mvc: MockMvc


    @Test
    fun generateTicketTest() {
        val value =  mvc.perform(MockMvcRequestBuilders.get(ROOT_API + GENERATE_TICKET)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val json = value.response.contentAsString
        assertNotNull(json)
        assertTrue(json.isNotEmpty())
    }

}
