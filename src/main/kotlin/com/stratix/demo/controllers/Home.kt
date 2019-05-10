package com.stratix.demo.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Controller
@RequestMapping("", "/")
class Home {

    @GetMapping
    fun getHomePage(model: Model) : String {
        return "home"
    }

    companion object {
        val HOME = "home"
    }

}