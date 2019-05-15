package com.stratix.demo

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.mongodb.*
import com.mongodb.client.MongoDatabase
import io.tekniq.config.*
import org.litote.kmongo.*
import org.litote.kmongo.util.KMongoConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AppConfig : TqChainConfig(
        TqEnvConfig(),
        TqPropertiesConfig("./config.properties", stopOnFailure = false),
        TqPropertiesConfig("/etc/stratix-demo/config.properties", stopOnFailure = false),
        TqPropertiesConfig("classpath:/config.properties")
) {
    private val logger: Logger = LoggerFactory.getLogger(AppConfig.javaClass)
    val mongoClient: MongoClient
    val mongo: MongoDatabase
    val keyPair: RsaKeyPair by lazy {
        get("keyPair") ?: CryptoUtil.generateKeyPair(2048)
                .also { save("keyPair", it) }
    }

    init {
        KMongoConfiguration.bsonMapper
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        KMongoConfiguration.extendedJsonMapper
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)

        val mongoUrl = MongoClientURI(get("database.url") ?: "mongodb://localhost/stratix-demo")
        logger.info("Establishing connection to $mongoUrl")

        mongoClient = KMongo.createClient(mongoUrl)
        mongo = mongoClient.getDatabase(mongoUrl.database ?: "stratix-demo")
    }

    override fun <T : Any?> getValue(key: String, type: Class<T>?): T? {
        if (contains(key)) {
            return super.getValue(key, type)
        }

        return mongo.getCollection("config")
                .findOneById(key)?.get("value")
                ?.let {
                    KMongoConfiguration.extendedJsonMapper.readValue(it.json, type)
                }
    }

    fun <T> save(key: String, value: T) {
        if (contains(key)) {
            throw IllegalStateException("Cannot change local override of $key")
        }
        mongo.getCollection<ConfigValue<T>>("config")
                .withWriteConcern(WriteConcern.MAJORITY)
                .save(ConfigValue(key, value))
    }

    fun destroy() {
        mongoClient.close()
    }
}

private data class ConfigValue<out T>(val _id: String, val value: T)
