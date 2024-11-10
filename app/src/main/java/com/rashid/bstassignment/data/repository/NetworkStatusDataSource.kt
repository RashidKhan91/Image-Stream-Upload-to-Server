package com.rashid.bstassignment.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


interface NetworkStatusDataSource {
    val isConnected: Flow<Boolean>
}

class NetworkStatusDataSourceImpl @Inject constructor(private val context: Context) : NetworkStatusDataSource {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _isConnected = MutableStateFlow(checkNetworkConnection())
    override val isConnected: Flow<Boolean> get() = _isConnected

    private fun checkNetworkConnection(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
