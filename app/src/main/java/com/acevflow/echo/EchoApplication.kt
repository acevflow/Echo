package com.acevflow.echo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Base [Application] class for Echo.
 * Initialized with Hilt for dependency injection.
 */
@HiltAndroidApp
class EchoApplication : Application()
