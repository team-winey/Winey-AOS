package com.android.go.sopt.winey.util.amplitude

import com.amplitude.api.AmplitudeClient
import com.amplitude.api.Identify
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AmplitudeUtils @Inject constructor(
    private val client: AmplitudeClient
) {
    init {
        setDefaultConfig()
    }

    private fun setDefaultConfig() {
        client.apply {
            trackSessionEvents(true)
            setEventUploadPeriodMillis(100)
        }
    }

    fun logEvent(eventType: String, eventProperties: JSONObject? = null) {
        if (eventProperties == null) {
            client.logEvent(eventType)
        } else {
            client.logEvent(eventType, eventProperties)
        }
    }

    fun <T : Any> setUserProperties(propertyName: String, values: T) {
        val identify = Identify().set(propertyName, values)
        client.identify(identify)
    }

    fun uploadEvents() {
        client.uploadEvents()
    }
}
