package dev.olog.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationScope @Inject constructor() : CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)