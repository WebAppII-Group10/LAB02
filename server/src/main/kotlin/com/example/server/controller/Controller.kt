package com.example.server.controller

import com.example.server.resources.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ROOT_API)
class Controller {

    @GetMapping(HOME)
    fun getHome(): String {
        return "Hello Group10 "
    }
}