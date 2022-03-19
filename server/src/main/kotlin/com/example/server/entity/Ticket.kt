package com.example.server.entity

import lombok.Getter
import lombok.Setter

@Setter
@Getter
class Ticket(private val zone: Char, private val token: String) {


    fun getZone(): Char{
        return this.zone
    }

    fun getToken(): String {
        return this.token
    }




}