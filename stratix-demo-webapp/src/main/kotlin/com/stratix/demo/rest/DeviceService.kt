package com.stratix.demo.rest

import com.stratix.demo.dao.Device
import com.stratix.demo.dao.DeviceDao
import com.stratix.demo.dao.OsType
import io.tekniq.web.*
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

object DeviceService {
    private val logger = LoggerFactory.getLogger(DeviceService::class.java)

    fun route(route: TqSparklinRoute) = route.apply {
        before { req, _ ->
            req.headers("Authorization")?.let { header ->
                if (!header.startsWith("Token")) {
                    return@before
                }
            }
        }

        get("/device") { req, res ->
            logger.debug("Device request received")

            var devices = DeviceDao.find()
            // Generate 5 devices if non exist
            if (devices.toList().size == 0) {
                logger.debug("Generating 5 devices")
                for( i in 1..5 ) {
                    val device = Device(
                            serialNumber = "%d%d%d%d%d-%d%d%d%d%d".format(i,i,i,i,i,i,i,i,i,i),
                            osType = OsType.iOS,
                            lastSeen = LocalDateTime.now(),
                            requiresMaintence = false
                    )
                    DeviceDao.saveDevice(device)
                }
                devices = DeviceDao.find()
            }

            devices
        }
    }
}

