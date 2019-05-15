package com.stratix.demo.dao

import com.mongodb.ReadPreference
import com.mongodb.WriteConcern
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.ReturnDocument
import com.stratix.demo.AppConfig
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.*
import java.time.LocalDateTime
import java.util.*

enum class OsType(val value: String) {
    iOS("ios"),
    Android("android"),
    WinPhone("winphone")
}
data class Device(
                  val serialNumber: String
                , val osType: OsType
                , val lastSeen: LocalDateTime
                , val requiresMaintence: Boolean?)

object DeviceDao: MongoCollection<Device> by AppConfig.mongo.getCollection<Device>()
        .withReadPreference(ReadPreference.primaryPreferred())
        .withWriteConcern(WriteConcern.JOURNALED) {

    fun saveDevice(device: Device) = findOneAndUpdate(
            filter = "{id: ${device.serialNumber.json}}",
            update = """{${MongoOperator.set}: {
                serialNumber: ${device.serialNumber.json},
                osType: ${device.osType.json},
                lastSeen: ${device.lastSeen.json},
                requiresMaintence: ${device.requiresMaintence?.json}
                }}""".trimIndent(),
            options = FindOneAndUpdateOptions()
                    .upsert(true)
        )
}