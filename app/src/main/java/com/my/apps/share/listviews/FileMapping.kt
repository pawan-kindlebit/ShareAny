package com.my.apps.share.listviews

import com.my.apps.share.R

object FileMapping {
  fun getDetails(fileType: String): Int {
    /*
    video, audio, photo, text, zip, binary
     */
    var icon = R.drawable.icon_binary
    when (fileType.lowercase()) {
      "mp4", "mkv", "avi" -> {
        icon = R.drawable.icon_video
      }
      "mp3", "wav", "aac", "ogg", "aiff" -> {
        icon = R.drawable.icon_audio
      }
      "jpeg", "jpg", "png", "webp" -> {
        icon = R.drawable.icon_image
      }
      "txt" -> {
        icon = R.drawable.icon_text
      }
      "zip", "gz" -> {
        icon = R.drawable.icon_zip
      }
      "pdf" -> {
        icon = R.drawable.icon_pdf
      }
      "ppt", "pptx" -> {
        icon = R.drawable.icon_ppt
      }
      "doc", "docx" -> {
        icon = R.drawable.icon_word
      }
    }
    return icon
  }
}