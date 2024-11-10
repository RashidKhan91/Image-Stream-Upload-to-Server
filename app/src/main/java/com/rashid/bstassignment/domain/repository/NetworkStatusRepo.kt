package com.rashid.bstassignment.domain.repository

import kotlinx.coroutines.flow.Flow

interface NetworkStatusRepo {
    val isConnected: Flow<Boolean>
}

