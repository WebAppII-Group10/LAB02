package com.example.server.entity


class Ticket(private val zone: Char, private val token: String) {


    fun getZone(): Char{
        return this.zone
    }

    fun getToken(): String {
        return this.token
    }

}