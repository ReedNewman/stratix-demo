package com.stratix.demo.rest

import com.stratix.demo.CryptographicMessage
import com.stratix.demo.dao.User
import com.stratix.demo.dao.UserDao
import io.tekniq.validation.Rejection
import io.tekniq.web.*
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import spark.Request
import java.util.*
import kotlin.random.Random

object UserService {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    private val authTokens = ThreadLocal<AuthToken?>()
    val authToken: AuthToken?
        get() = authTokens.get()

    fun route(route: TqSparklinRoute) = route.apply {
        before { req, _ ->
            req.headers("Authorization")?.let { header ->
                if (!header.startsWith("Token ")) {
                    // Safe compatibility with any mixed authorization concepts
                    return@before
                }

                header.substring("Token ".length).also { token ->
                    var expiration: Date? = null
                    var authn: ObjectId? = null
                    val authz: MutableSet<String> = mutableSetOf()
                    CryptographicMessage(token).decrypted
                            .split("&")
                            .mapNotNull {
                                val pieces = it.split("=", limit = 2)
                                if (pieces.size == 2) {
                                    Pair(pieces[0], pieces[1])
                                } else {
                                    null
                                }
                            }.forEach { (key, value) ->
                                when (key) {
                                    "TTL" -> expiration = Date(value.toLong())
                                    "A1" -> authn = ObjectId(value)
                                    "A2" -> authz += value
                                }
                            }.let {
                                AuthToken(permissions = authz,
                                        ttl = expiration
                                                ?: throw NotAuthorizedException(listOf(Rejection("authExpired"))),
                                        userId = authn
                                                ?: throw NotAuthorizedException(listOf(Rejection("authNotFound")))
                                )
                            }.also { authTokens.set(it) }
                            .also { req.attribute("AuthToken", it) }
                }
            }
        }

        post("/user/authenticate") { req, res ->
            logger.debug("Authentication Request")
            val validation = JsonRequestValidation(req)
                    .email("email")
                    .required("password")
                    .stopOnRejections()
            val login = req.jsonAs<LoginRequest>()
            logger.debug("Credentials: $login")
            UserDao.findByAuthentication(login.email, login.password)
                    ?.let { user ->
                        val token = StringBuilder("TTL=${login.expiration.time}").apply {
                            append("&A1=${user.id.toHexString()}")
                            append("&A2=AUTHENTICATED")
                            if (user.admin) {
                                append("&A2=ADMIN")
                            }
                        }.toString()

                        LoginResponse(token = CryptographicMessage(token).encrypted,
                                firstName = user.firstName ?: "",
                                lastName = user.lastName ?: "",
                                admin = user.admin,
                                expiration = login.expiration)
                    }
                    ?: validation.reject("notFound").stop()
        }
    }
}

object UserAuthManager : AuthorizationManager {
    private val logger = LoggerFactory.getLogger("StdOut")

    init {
        val characterPool: List<Char> = ('0'..'z').toList()
        val generatedPassword = (1..16)
                .map { characterPool[Random.nextInt(0, characterPool.size)] }
                .joinToString("")
        val user = User(ObjectId.get(), admin = true,
                email = "system@stratixcorp.com", password = generatedPassword,
                firstName = "Admin", lastName = "Stratix")
        logger.info("Administration account is being regenerated for security reasons")
        UserDao.saveUser(user)?.also {
            UserDao.updatePassword(it.email, generatedPassword)
            logger.info("   System Email: ${user.email}")
            logger.info("System Password: ${user.password}\n")
        }
    }

    override fun getAuthz(request: Request): Collection<String> = request
            .attribute<AuthToken>("AuthToken")
            ?.permissions
            ?: emptySet()
}

data class AuthToken(val ttl: Date, val userId: ObjectId, val permissions: Set<String>)

data class LoginRequest(val email: String,
                        val expiration: Date = Date(System.currentTimeMillis() + 1000L * 60 * 60)) {
    lateinit var password: String
}

data class LoginResponse(val token: String,
                         val firstName: String,
                         val lastName: String,
                         val admin: Boolean,
                         val expiration: Date)
