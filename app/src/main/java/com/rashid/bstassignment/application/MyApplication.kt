package com.rashid.bstassignment.application

import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rashid.bstassignment.data.worker.ImageUploadWorker
import com.rashid.bstassignment.utils.NetworkReceiver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val networkReceiver = NetworkReceiver {
            // Trigger WorkManager to upload images when network is available
            startImageUploadWork()
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, intentFilter)
    }

    private fun startImageUploadWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<ImageUploadWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(uploadWorkRequest)

    }

}