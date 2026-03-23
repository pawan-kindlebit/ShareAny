package com.my.apps.share.core.files

import com.my.apps.share.core.transfer.ChannelInfo
import com.my.apps.share.core.transfer.MultiChannelStream
import com.my.apps.share.core.TaskExecutor
import com.my.apps.share.core.io.DataInputStream
import com.my.apps.share.core.io.DataInputStream.ByteListener

class RequestHandler(private val listener: (FileReceiver) -> Unit) {

  private lateinit var reader: MultiChannelStream
  private lateinit var exec: TaskExecutor

  private val requests = DataInputStream()

  fun setReader(reader: MultiChannelStream, executor: TaskExecutor) {
    this.reader = reader
    this.exec = executor

    init()
  }

  private fun init() {
    requests.byteListener = ByteListener {
      val port = requests.readInt32()

      val lengthOfFile = requests.readInt32()

      val nameStringLength = requests.readInt32()
      val fileName = ByteArray(nameStringLength)

      requests.read(fileName)
      val name = String(fileName)

      listener.invoke(
        FileReceiver(
          port,
          name,
          lengthOfFile
        )
      )
      // true because, we do not need rest of the
      // byte listen calls
      true
    }
    reader.registerChannelStream(ChannelInfo.FILE_REQUEST_CHANNEL_INFO, requests)
  }

  fun destroy() {
    reader.forget(ChannelInfo.FILE_REQUEST_CHANNEL_INFO)
    requests.byteListener = null
  }
}