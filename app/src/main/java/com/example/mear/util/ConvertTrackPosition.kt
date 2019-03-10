package com.example.mear.util


class ConvertTrackPosition(val percentage: Int) {

    fun newPosition(trackDuration: Int): Int {
        val newPosition = (trackDuration * (percentage.toDouble() / 100)).toInt()

        return newPosition
    }
}