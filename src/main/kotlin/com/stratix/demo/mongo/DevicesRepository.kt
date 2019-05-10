package com.stratix.demo.mongo

import com.stratix.demo.model.Device
import org.springframework.data.mongodb.repository.MongoRepository

interface DevicesRepository: MongoRepository<Device, String>
