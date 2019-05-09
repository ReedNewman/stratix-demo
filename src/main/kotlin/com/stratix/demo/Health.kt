package com.stratix.demo

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

data class HealthMessage(val message: String, val status: Int)

@RestController
@RequestMapping("/health")
class Health {
    @RequestMapping("", "/", method = [RequestMethod.GET])
    fun processHealthCheck() : HealthMessage {
        return HealthMessage("All good", 200)
    }
}