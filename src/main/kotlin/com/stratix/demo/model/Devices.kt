package com.stratix.demo.model

import java.util.*

data class Devices(
        val serial : String,
        val os : String,
        val lastSeen : Date,
        val requiresMaintenence : Boolean
)