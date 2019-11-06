package com.example.mear.repositories

import android.content.Context

import com.example.mear.constants.CPPLib


class ShuffleRepository(var context: Context?) {

    companion object {
        init {
            System.loadLibrary(CPPLib.NATIVE_LIB)
        }
    }


    private external fun retrieveShuffleMode(path: String): Int

    private external fun updateShuffle(path: String)


    fun shuffleMode(path: String): ShuffleTypes {
        val shuffleType = ShuffleTypes.valueOf(retrieveShuffleMode(path))

        return shuffleType!!
    }


    fun alterShuffleMode(path: String) {
        updateShuffle(path)
    }


    enum class ShuffleTypes(val value: Int) {
        ShuffleOn(0),
        ShuffleOff(1);

        companion object {
            fun valueOf(value: Int) = values().find { it.value == value }
        }
    }
}