package dev.olog.navigation.dagger

import dagger.MapKey
import dev.olog.navigation.screens.Services

@MapKey
annotation class ServiceKey(val value: Services)