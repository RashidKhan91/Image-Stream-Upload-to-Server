package com.rashid.bstassignment.domain.useCases.impl

import com.rashid.bstassignment.domain.repository.NetworkStatusRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNetworkStatusUseCaseImp @Inject constructor(
    private val networkStatusRepository: NetworkStatusRepo
) {
    operator fun invoke(): Flow<Boolean> = networkStatusRepository.isConnected
}