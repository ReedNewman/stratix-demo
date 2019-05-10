package com.stratix.demo

import com.stratix.demo.controllers.Home
import com.stratix.demo.mongo.DevicesRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DemoApplicationTests {

	@Mock
	private lateinit var device: DevicesRepository

	private lateinit var home: Home

	@BeforeAll
	fun setup() {
		home = Home(device)
	}

	@Test
	fun contextLoads() {
	}

	@Test
	fun `10 devices are created preperly` () {
		val devices = home.initIfNoData()
		assert(devices.size == Home.GENERATED_DEVICES)
	}
}
