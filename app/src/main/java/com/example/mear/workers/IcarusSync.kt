package com.example.mear.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters


class IcarusSyncManager(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        //TODO perform your async operational task here
        /**
         * We have performed download task here on above example
         */
        val accessToken = inputData.getString("accessToken")
        val appPath = inputData.getString("appPath")
        //val songId = inputData.getInt("songId")

        return Result.success()
    }
}