package com.stratix.demo.controllers

import com.stratix.demo.model.Device
import com.stratix.demo.model.OsType
import com.stratix.demo.mongo.DevicesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@Controller
@RequestMapping("", "/")
class Home (
        @Autowired private val devicesRepository : DevicesRepository
) {

    @GetMapping
    fun getHomePage(model: Model) : String {
        val dataToSave = initIfNoData()
        if (!dataToSave.isNullOrEmpty()) {
            saveDataToDatabase(dataToSave)
        }

        model.apply {
            addAttribute("devices", devicesRepository.findAll())
        }

        return HOME
    }

    fun initIfNoData() : List<Device> {
        if ( devicesRepository.findAll().size != 0 ) {
            return devicesRepository.findAll()
        }

        var devices = arrayListOf<Device>()

        // Initial boot, let's populate our data
        for ( i in 1..GENERATED_DEVICES ) {
            val osType = OsType.fromInt((0..2).random())

            val offset = Timestamp.valueOf("2018-12-31 00:00:00").getTime()
            val end = Timestamp.valueOf("2019-06-30 00:00:00").getTime()
            val diff = end - offset + 1
            val lastSeen = Timestamp(offset + (Math.random() * diff).toLong())

            val device = Device(
                    UUID.randomUUID().toString(),
                    osType,
                    lastSeen,
                    Random.nextBoolean()
            )

            devices.add(device)
        }
        return devices
    }

    fun saveDataToDatabase(devices : List<Device>) {
        for (device in devices) {
            devicesRepository.save(device)
        }
    }

    companion object {
        val HOME = "home"
        val GENERATED_DEVICES = 10
    }

}