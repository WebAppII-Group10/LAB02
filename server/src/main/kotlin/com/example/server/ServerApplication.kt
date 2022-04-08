package com.example.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServerApplication

/**
 *
 * Server Application EntryPoint
 *
 */
fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
