package com.stratix.demo.mongo

import com.mongodb.MongoClient
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.config.java.AbstractCloudConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.util.*

@Profile("!cloud")
@Configuration
@EnableMongoRepositories
class MongoConfig : AbstractMongoConfiguration() {
    override fun getDatabaseName() = "stratix"
    @Bean
    override fun mongoClient() = MongoClient("localhost")
    @Bean
    override fun mongoTemplate() = MongoTemplate(mongoClient(), databaseName)
}

@Profile("cloud")
@Configuration
@EnableMongoRepositories
class CloudMongoConfiguration: AbstractCloudConfig() {

    @Value("\${stratix.mongo.service-name}")
    val mongoDbInstanceId: String = "mongo-stratix"

    @Bean
    fun mongDbFactory(): MongoDbFactory {
        return connectionFactory().mongoDbFactory(mongoDbInstanceId)
    }

    @Bean
    fun customConversions(): MongoCustomConversions {
        return MongoCustomConversions(mutableListOf<Collections>())
    }

    @Bean
    @Throws(ClassNotFoundException::class)
    fun mongoMappingContext(): MongoMappingContext {
        val context = MongoMappingContext()
        context.setSimpleTypeHolder(customConversions().simpleTypeHolder)
        return context
    }

    @Bean
    @Throws(ClassNotFoundException::class)
    fun mappingMongoConverter(beanFactory: BeanFactory): MappingMongoConverter {
        val dbRefResolver = DefaultDbRefResolver(mongDbFactory())
        val mappingConverter = MappingMongoConverter(dbRefResolver, mongoMappingContext())
        mappingConverter.setCustomConversions(customConversions())
        return mappingConverter
    }

    @Bean
    @Throws(ClassNotFoundException::class)
    fun mongoTemplate(beanFactory: BeanFactory): MongoTemplate {
        return MongoTemplate(mongDbFactory(), mappingMongoConverter(beanFactory))
    }
}