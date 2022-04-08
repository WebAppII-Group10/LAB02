package com.example.server.entity

/**
 * TicketSubmitted class:
 * By design a TicketSubmitted object is composed by the token itself used as authentication factor
 * and a submitted zone
 */
class TicketSubmitted(private val zone: Char, private val token: String) {


    fun getZone(): Char{
        return this.zone
    }

    fun getToken(): String {
        return this.token
    }

}