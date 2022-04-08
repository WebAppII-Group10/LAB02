package com.example.server.service

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 *
 * Service is used to handle the collection of committed ticket by means of a relationship (ticketID, committedFlag)
 *
 */
@Service
class TicketService {

    //Declare empty ConcurrentMap
    private val ticketPool : ConcurrentMap<Long, Boolean> = ConcurrentHashMap()
    //static counter by means of service
    private var currentTicketId : Long = 1
    //Suffix for the string
    private val suffix = "TICKET_"

    /**
     *
     * Generate a ticketId and add it to the collection with true committedFlag
     * Update ticketId index
     * @return current ticketId: String (format: TICKET_<ticketID>)
     *
     */
    fun getNewTicketId(): String{
        //add current ticket
        ticketPool[currentTicketId] = true
        return "$suffix${currentTicketId++}"
    }

    /**
     *
     * InvalidTicket receive a ticketId and check if the ticket is committed (true)
     * then update the collection (set flag to false for that ticket)
     * otherwise return false
     * @return:
     *  True: ticket is not committed
     *  False: ticket is already committed
     *
     */
    @Synchronized
    fun invalidTicket(ticketId: Long): Boolean {
        if(this.ticketPool.containsKey(ticketId))
            if(this.ticketPool[ticketId] == true){
                //Update ticketId status
                this.ticketPool[ticketId] = false
                return true
            }
        return false
    }

    /**
     *
     * getter for the ticketId
     * @return: current ticketId: Long
     *
     */
    fun getCurrentId(): Long {
        return this.currentTicketId
    }

}