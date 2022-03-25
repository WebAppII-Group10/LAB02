package com.example.server.service

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


@Service
class TicketService {

    //Declare empty Map
    private val ticketPool : ConcurrentMap<Long, Boolean> = ConcurrentHashMap()
    //static counter by means of service
    private var currentTicketId : Long = 1
    //Suffix for the string
    private val suffix = "TICKET_"

    fun getNewTicketId(): String{
        ticketPool[currentTicketId] = true
        return "$suffix${currentTicketId++}"
    }

    fun invalidTicket(ticketId: Long): Boolean {
        if(this.ticketPool.containsKey(ticketId))
            if(this.ticketPool[ticketId] == true){
                //Update ticketId status
                this.ticketPool[ticketId] = false
                return true
            }
        return false
    }


}