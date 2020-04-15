package dev.olog.navigation.screens

import android.app.Activity
import android.app.Service
import android.appwidget.AppWidgetProvider

typealias ServicesMap = Map<Services, @JvmSuppressWildcards Class<out Service>>
typealias ActivitiesMap = Map<Activities, @JvmSuppressWildcards Class<out Activity>>
typealias WidgetsMap = Map<Widgets, @JvmSuppressWildcards Class<out AppWidgetProvider>>