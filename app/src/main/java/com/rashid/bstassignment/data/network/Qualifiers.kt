package com.rashid.bstassignment.data.network

import javax.inject.Qualifier

//qualifier we used usually to add network request concurrently at multiple servers.

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Auth

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Delivery
