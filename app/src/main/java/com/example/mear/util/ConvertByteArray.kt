package com.example.mear.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class ConvertByteArray(private val byteArray: ByteArray) {

    fun convertToBmp(): Bitmap {
        val songImage = BitmapFactory
            .decodeByteArray(byteArray, 0, byteArray.size)

        return songImage
    }
    fun convertToBmptScales(width: Int, height: Int): Bitmap {
        val b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        return Bitmap.createScaledBitmap(b, width, height, false)
    }
}