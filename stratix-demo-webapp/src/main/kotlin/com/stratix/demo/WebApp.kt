package com.stratix.demo

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.stratix.demo.notification.NotificationWebSocket
import com.stratix.demo.rest.DeviceService
import com.stratix.demo.rest.UserAuthManager
import com.stratix.demo.rest.UserService
import io.tekniq.validation.Rejection
import io.tekniq.web.*
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import spark.servlet.SparkApplication
import kotlin.concurrent.thread

fun main() {
    println("Initializing Web Application")
    val app = WebApp().apply { init() }
    Runtime.getRuntime().addShutdownHook(thread(false, name = "Webapp Shutdown Hook") {
        app.destroy()
        AppConfig.mongoClient.close()
    })
}

class WebApp : SparkApplication {
    private val logger = LoggerFactory.getLogger(WebApp::class.java)

    override fun init() {
        sparklinMapper
                .registerModule(ObjectIdModule)
                .disable(SerializationFeature.WRITE_NULL_MAP_VALUES)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        val staticFiles = when {
            AppConfig.contains("EXTERNAL_FILES") -> {
                logger.info("External WebApp Files at ${AppConfig.get<String>("EXTERNAL_FILES")}")
                TqSparklinStaticFiles(externalFileLocation = AppConfig.get("EXTERNAL_FILES"))
            }
            else -> {
                logger.info("Internal WebApp Files at classpath:/")
                TqSparklinStaticFiles(fileLocation = "/")
            }
        }

        val config = TqSparklinConfig(staticFiles = staticFiles,
                authorizationManager = UserAuthManager)

        TqSparklin(config) {
            webSocket("/notification", NotificationWebSocket::class)

            before { req, res ->
                res.type("application/json")

                if (req.requestMethod() == "OPTIONS") {
                    res.status(204)
                    res.body()
                }
            }

            afterAfter { req, res ->
                res.apply {
                    header("Access-Control-Allow-Origin", req.headers("Origin") ?: "*")
                    header("Access-Control-Allow-Methods", req.headers("Access-Control-Request-Method")
                            ?: "GET, POST, PUT, DELETE, OPTIONS")
                    val acrh = req.headers("Access-Control-Request-Headers")
                    if (acrh != null) {
                        header("Access-Control-Allow-Headers", acrh)
                    }
                    header("Access-Control-Max-Age", "10")
                }
            }

            options("*") { _, _ -> }

            UserService.route(this)
            DeviceService.route(this)

            exception(NotAuthorizedException::class) { e, _, _ ->
                Pair(401, mapOf("errors" to e.rejections))
            }

            exception(Exception::class) { e, _, _ ->
                logger.error(e.message, e)
                Pair(500, mapOf("errors" to listOf(Rejection(
                        code = "unknown",
                        message = e.message ?: "Unexpected error $e"))))
            }

            notFound { req, _ ->
                mapOf("errors" to listOf(Rejection(
                        code = "notFound",
                        field = "${req.requestMethod()} ${req.pathInfo()}")))
            }
        }.apply {
            println("Application started up on ${config.ip}:${config.port}")
        }
    }
}

private object ObjectIdModule : SimpleModule("ObjectIdModule") {
    init {
        addSerializer(ObjectId::class.java, object : JsonSerializer<ObjectId>() {
            override fun serialize(value: ObjectId?, gen: JsonGenerator, serializers: SerializerProvider?) = gen.writeString(value?.toHexString())
        })
    }
}
