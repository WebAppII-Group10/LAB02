package com.example.server.service

import org.springframework.stereotype.Service


@Service
class TicketService {

    //Declare empty Map
    private val ticketPool = mutableMapOf<Int, Boolean>()
    //static counter by means of service
    private var currentTicketId = 1
    //Suffix for the string
    private val suffix = "TICKET_"

    fun getNewTicketId(): String{
        ticketPool[currentTicketId] = true
        return "$suffix${currentTicketId++}"
    }

    fun invalidTicket(ticketId: Int): Boolean {
        if(this.ticketPool.containsKey(ticketId))
            if(this.ticketPool[ticketId] == true){
                //Update ticketId status
                this.ticketPool[ticketId] = false
                return true
            }
        return false
    }


}