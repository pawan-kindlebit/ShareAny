package com.my.apps.share.core

import android.content.Context
import com.my.apps.share.core.transfer.MultiChannelStream
import com.my.apps.share.core.transfer.MultiChannelSystem
import com.my.apps.share.core.files.FileRequest
import com.my.apps.share.core.files.RequestHandler
import com.my.apps.share.core.io.DataInputStream
import com.my.apps.share.core.transfer.ChannelInfo
import com.my.apps.share.core.transfer.SocketConnection

class TaskExecutor(connection: SocketConnection) {

  val reader =
    MultiChannelStream(connection.input)
  val writer =
    MultiChannelSystem(connection.output)

  private var handler: RequestHandler? = null

  init {
    reader.start()
    writer.start()
  }

  fun execute(context: Context, fileRequest: FileRequest) {
    fileRequest.execute(context, this)
  }

  // called when Session class is started
  // this looks for incoming file requests that contains
  // the file name followed by the file length
  fun register(handler: RequestHandler) {
    handler.setReader(reader, this)
    this.handler = handler
  }

  fun unregister() {
    handler?.destroy()
  }

  fun respond(channel: ChannelInfo) {
    writer.write(
      channel,
      ByteArray(1)
    )
  }

  fun respond(channel: ChannelInfo, byte: Byte) {
    writer.write(channel, byteArrayOf(byte))
  }

  fun register(channel: ChannelInfo, forgetAfter: Boolean, listener: (Byte) -> Unit) {
    val dataInputStream = DataInputStream()

    reader.registerChannelStream(
      channel,
      dataInputStream,
      true
    )

    dataInputStream.setByteListener {
      listener.invoke(it)
      if (forgetAfter)
        reader.forget(channel)
      return@setByteListener true
    }
  }

  fun stopStreams() {
    reader.stop()
    writer.stop()
  }
}