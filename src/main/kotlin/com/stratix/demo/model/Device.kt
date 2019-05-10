package com.stratix.demo.model

import java.util.*

data class Device(
        val serial : String,
        val os : OsType,
        val lastSeen : Date,
        val requiresMaintenence : Boolean
)