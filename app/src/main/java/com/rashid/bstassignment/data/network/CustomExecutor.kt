package com.rashid.bstassignment.data.network

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object CustomExecutor {
    fun createExecutor(maxConcurrency: Int): ExecutorService {
        return Executors.newFixedThreadPool(maxConcurrency)
    }
}
