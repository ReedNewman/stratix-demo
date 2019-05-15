package com.stratix.demo.dao

import com.mongodb.ReadPreference
import com.mongodb.WriteConcern
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.*
import com.stratix.demo.AppConfig
import com.stratix.demo.rest.AuthToken
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.MongoOperator.set
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*

data class User(@BsonId val id: ObjectId,
                val email: String,
                val password: String? = null,
                val admin: Boolean = false,
                val firstName: String? = null,
                val lastName: String? = null)

object UserDao : MongoCollection<User> by AppConfig.mongo.getCollection<User>()
        .withReadPreference(ReadPreference.primaryPreferred())
        .withWriteConcern(WriteConcern.JOURNALED) {
    init {
        createIndex("{email: 1}", indexOptions = IndexOptions().unique(true))
    }

    fun findByAuthentication(email: String, password: String): User? =
            findOne("{email: ${email.json}}")?.let { user ->
                val hashSha256 = hashSha256("${user.id.toHexString()}$password")
                if (user.password == hashSha256) {
                    user
                } else {
                    null
                }
            }

    fun findOneById(token: AuthToken) = findOneById(token.userId)
    fun saveUser(user: User) = findOneAndUpdate(
            filter = "{email: ${user.email.json}}",
            update = """{$set: {
                email: ${user.email.json},
                firstName: ${user.firstName?.json},
                lastName: ${user.lastName?.json},
                admin: ${user.admin.json}
                }}""".trimIndent(),
            options = FindOneAndUpdateOptions()
                    .upsert(true)
                    .returnDocument(ReturnDocument.AFTER))

    fun updatePassword(email: String, password: String): Boolean {
        return findOne("{email: ${email.json}}")
                ?.let {
                    UserDao.findOneAndUpdate(
                            filter = "{_id: ${it.id.json}}",
                            update = "{$set: {password: ${hashSha256("${it.id.toHexString()}$password").json}}}")
                } != null
    }

    private fun hashSha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray())
        return Base64.getEncoder().encode(hash)?.toString(Charset.defaultCharset())!!
    }
}
