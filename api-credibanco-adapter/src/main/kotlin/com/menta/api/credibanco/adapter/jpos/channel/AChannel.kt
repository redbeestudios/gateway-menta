package com.menta.api.credibanco.adapter.jpos.channel

import org.jpos.iso.BaseChannel
import org.jpos.iso.ISOException
import java.io.IOException

class AChannel : BaseChannel() {
    @Throws(IOException::class, ISOException::class)
    override fun getMessageLength(): Int {
        var l = 0
        val b = ByteArray(2)
        while (l == 0) {
            serverIn.readFully(b, 0, 2)
            l = ((b[0].toInt() and 0xFF) shl 8) or (b[1].toInt() and 0xFF)
        }
        return l
    }

    @Throws(IOException::class)
    override fun sendMessageLength(len: Int) {
        serverOut.write(len shr 8)
        serverOut.write(len)
    }
}
