package com.rashid.bstassignment.application

import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager

import com.rashid.bstassignment.utils.NetworkReceiver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val networkReceiver = NetworkReceiver {
            // Trigger WorkManager to upload images when network is available
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, intentFilter)
    }



}