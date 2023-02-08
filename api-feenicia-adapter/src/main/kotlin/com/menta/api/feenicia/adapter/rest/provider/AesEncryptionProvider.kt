package com.menta.api.feenicia.adapter.rest.provider

import org.apache.commons.codec.DecoderException
import org.apache.commons.codec.binary.Hex
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.EncryptionException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.Charset
import java.security.Security
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.Cipher.getInstance
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class AesEncryptionProvider(
    @Value("\${api-feenicia-adapter.aes.encoding}")
    private val encoding: String,
    @Value("\${api-feenicia-adapter.aes.mode}")
    private val mode: String
) {
    /* INFORMATION CIPHERING @return encodeBase24 **/
    fun encrypt(key: String, iv: String, data: String? = ""): String {
        Security.addProvider(BouncyCastleProvider())
        val output: ByteArray?
        try {
            val keyBytes = decode(key)
            val input: ByteArray = data!!.toByteArray(Charset.forName(encoding))
            val ivSpec: AlgorithmParameterSpec = IvParameterSpec(Hex.decodeHex(iv.toCharArray()))
            val keySpec = SecretKeySpec(keyBytes, "AES")
            val cipher = getInstance(mode)
            cipher.init(ENCRYPT_MODE, keySpec, ivSpec)
            output = cipher.doFinal(input)
        } catch (e: Exception) {
            throw EncryptionException("Error when trying to encrypt", e)
        }
        return encode(output)
    }

    fun decrypt(key: String, iv: String, data: String?): String {
        val output: ByteArray?
        try {
            val keyBytes = decode(key)
            val input = decode(data!!)
            val keySpec = SecretKeySpec(keyBytes, "AES")
            val cipher = getInstance(mode)
            val ivSpec = IvParameterSpec(Hex.decodeHex(iv.toCharArray()))
            cipher.init(DECRYPT_MODE, keySpec, ivSpec)
            output = cipher.doFinal(input)
        } catch (e: java.lang.Exception) {
            throw EncryptionException("Error when trying to decrypt", e)
        }
        return String(output)
    }

    private fun encode(output: ByteArray?): String = String(Hex.encodeHex(output))

    @Throws(DecoderException::class)
    private fun decode(data: String): ByteArray = Hex.decodeHex(data.toCharArray())
}
