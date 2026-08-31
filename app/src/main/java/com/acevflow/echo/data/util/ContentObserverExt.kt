package com.acevflow.echo.data.util

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

/**
 * Wraps a [ContentObserver] in a [Flow] that emits when the given [uri] changes.
 */
fun ContentResolver.observe(uri: Uri): Flow<Unit> = callbackFlow {
    val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            trySend(Unit)
        }
    }
    
    registerContentObserver(uri, true, observer)
    
    awaitClose {
        unregisterContentObserver(observer)
    }
}.onStart { emit(Unit) } // Emit immediately to trigger initial load
