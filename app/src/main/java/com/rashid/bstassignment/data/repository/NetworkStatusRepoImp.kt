package com.rashid.bstassignment.data.repository

import com.rashid.bstassignment.domain.repository.NetworkStatusRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class NetworkStatusRepoImp @Inject constructor(
    private val networkStatusDataSource: NetworkStatusDataSource
) : NetworkStatusRepo {
    override val isConnected: Flow<Boolean> = networkStatusDataSource.isConnected
}