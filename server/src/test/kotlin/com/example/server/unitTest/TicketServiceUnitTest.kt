package com.example.server.unitTest

import com.example.server.service.TicketService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TicketServiceUnitTest {

    @Autowired
    private lateinit var ticketService: TicketService

    //get new ticket
    @Test
    fun getNewTicketTest(){
        val t = ticketService.getNewTicketId()
        Assertions.assertTrue(t.split("_")[0] == "TICKET")
        Assertions.assertTrue(t.split("_")[1] == (ticketService.getCurrentId() - 1).toString())
    }

    //invalid the ticket
    @Test
    fun invalidTicketTest(){
        ticketService.getNewTicketId()
        //invalid ticket correctly
        Assertions.assertTrue(ticketService.invalidTicket(1))
        //invalid already not-valid ticket
        Assertions.assertFalse(ticketService.invalidTicket(1))
        //invalid not present ticket
        Assertions.assertFalse(ticketService.invalidTicket(100))
    }
}