package com.my.apps.share.core

import android.util.Log
import com.my.apps.share.core.io.BitOutputStream
import com.my.apps.share.core.io.ImplBitInputStream
import com.my.apps.share.core.transfer.SocketConnection
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

object KnowEachOther {
  private const val TAG = "KnowEachOther"

  fun initiate(
    data: String,
    connector: SocketConnection,
    knew: (String, String) -> Unit
  ) {
    val nameContent = data.toByteArray()

    BitOutputStream(connector.output).apply {
      writeShort16(nameContent.size.toShort())
      write(nameContent)
      flush()
    }

    val executor = ScheduledThreadPoolExecutor(1)
    executor.schedule({
      val bitInput = ImplBitInputStream(connector.input)
      val length = bitInput.readShort16()

      val buffer = ByteArray(length)

      var offset = 0
      while (offset != length)
        offset += bitInput.read(buffer, offset, length - offset)

      val other = String(buffer)

      Log.d(TAG, "Other: $other")
      knew.invoke(other, connector.socket.inetAddress.hostAddress!!)

      executor.shutdownNow()
    }, 10, TimeUnit.MILLISECONDS)
  }
}