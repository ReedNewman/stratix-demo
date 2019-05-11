package com.stratix.demo

import java.io.*
import java.math.BigInteger
import java.security.*
import java.security.spec.*
import java.util.*
import javax.crypto.Cipher

data class CryptographicMessage(val message: String, val key: RsaKeyPair = AppConfig.keyPair) {
    val decrypted: String by lazy {
        key.privateRsaKey
                ?.let { CryptoUtil.transform(message, Cipher.DECRYPT_MODE, it) }
                ?: throw UnsupportedOperationException("Private Key not found")
    }
    val encrypted: String by lazy {
        key.publicRsaKey
                ?.let { CryptoUtil.transform(message, Cipher.ENCRYPT_MODE, it) }
                ?: throw UnsupportedOperationException("Public Key not found")
    }

    val signed: String by lazy {
        key.privateRsaKey
                ?.let { CryptoUtil.transform(message, Cipher.ENCRYPT_MODE, it) }
                ?: throw UnsupportedOperationException("Private Key not found")
    }
    val verified: String by lazy {
        key.publicRsaKey
                ?.let { CryptoUtil.transform(message, Cipher.DECRYPT_MODE, it) }
                ?: throw UnsupportedOperationException("Public Key not found")
    }
}

object CryptoUtil {
    fun generateKeyPair(bits: Int = 2048): RsaKeyPair = KeyFactory.getInstance("RSA")
            .let {
                val keyPair = KeyPairGenerator.getInstance("RSA")
                        .let { generator ->
                            generator.initialize(bits)
                            generator.generateKeyPair()
                        }

                val privateKey = it.getKeySpec(keyPair.private, RSAPrivateKeySpec::class.java)
                val publicKey = it.getKeySpec(keyPair.public, RSAPublicKeySpec::class.java)
                RsaKeyPair(PrivateKeySpec(privateKey.modulus, privateKey.privateExponent),
                        PublicKeySpec(publicKey.modulus, publicKey.publicExponent))
            }

    internal fun transform(message: String, mode: Int, key: KeySpec): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(mode, generateKeyFromSpec(key))
        val bais = ByteArrayInputStream(message.toByteArray())
        val baos = ByteArrayOutputStream()
        if (Cipher.DECRYPT_MODE == mode) {
            decode(bais, baos, cipher)
        } else {
            encode(bais, baos, cipher, getBlockSize(key))
        }
        return baos.toString()
    }

    private fun decode(input: InputStream, output: OutputStream, cipher: Cipher) {
        val message = StringBuilder()
        while (true) {
            val buffer = ByteArray(4096)
            val read = input.read(buffer)
            if (read <= 0) {
                break
            }

            message.append(String(buffer, 0, read))

            while (true) {
                val indexOf = message.indexOf(" ")
                if (indexOf <= 0) {
                    break
                }

                val block = message.substring(0, indexOf).toByteArray()
                message.replace(0, indexOf + 1, "") // wipe out the processed portion and leave the com.stratix.demo.rest
                decodeHelper(block, output, cipher)
            }
        }
        if (message.isNotEmpty()) {
            decodeHelper(message.toString().toByteArray(), output, cipher)
        }
    }

    private fun decodeHelper(block: ByteArray, out: OutputStream, cipher: Cipher) {
        val decode = Base64.getDecoder().decode(block)
        out.write(cipher.doFinal(decode))
    }

    private fun encode(input: InputStream, output: OutputStream, cipher: Cipher, blockSize: Int) {
        var prepend = false
        val buffer = ByteArray(blockSize)
        while (true) {
            val read = input.read(buffer)
            if (read <= 0) {
                break
            }
            if (prepend) {
                output.write(" ".toByteArray())
            } else {
                prepend = true
            }

            val encrypted = cipher.doFinal(buffer, 0, read)
            val encode = Base64.getEncoder().encode(encrypted)
            output.write(encode)
        }
    }

    private fun generateKeyFromSpec(spec: KeySpec): Key? = KeyFactory.getInstance("RSA")
            ?.let {
                when (spec) {
                    is RSAPublicKeySpec -> it.generatePublic(spec)
                    is RSAPrivateKeySpec -> it.generatePrivate(spec)
                    else -> null
                }
            }


    private fun getBlockSize(spec: KeySpec): Int = when (spec) {
        is RSAPublicKeySpec -> spec.modulus.bitLength() / 8 - 11
        is RSAPrivateKeySpec -> spec.modulus.bitLength() / 8 - 11
        else -> throw UnsupportedOperationException("Only RSA Key Specs are supported!")
    }
}

class PrivateKeySpec(val modulus: BigInteger, val privateExponent: BigInteger) : KeySpec
class PublicKeySpec(val modulus: BigInteger, val publicExponent: BigInteger) : KeySpec
data class RsaKeyPair(val privateKey: PrivateKeySpec?, val publicKey: PublicKeySpec?) {
    val privateRsaKey: RSAPrivateKeySpec? by lazy {
        privateKey?.let { RSAPrivateKeySpec(it.modulus, it.privateExponent) }
    }
    val publicRsaKey: RSAPublicKeySpec? by lazy {
        publicKey?.let { RSAPublicKeySpec(it.modulus, it.publicExponent) }
    }
}

