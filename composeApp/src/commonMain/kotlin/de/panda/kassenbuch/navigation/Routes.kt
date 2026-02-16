package de.panda.kassenbuch.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object SettingsRoute

@Serializable
data class BookingFormRoute(val bookingId: Long? = null)

@Serializable
data object CoinCounterRoute
