package com.openclassrooms.vitesse.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object CurrencyApi {
  // Per updated spec: fetch EUR JSON and read eur.gbp
  private val BASE_URLS = listOf(
    "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json",
    "https://latest.currency-api.pages.dev/v1/currencies/eur.json"
  )

  suspend fun fetchEurToGbpRate(): Double? = withContext(Dispatchers.IO) {
    for (endpoint in BASE_URLS) {
      var url = URL(endpoint)
      var connection: HttpURLConnection? = null
      try {
        repeat(2) {
          connection = (url.openConnection() as HttpURLConnection).apply {
            instanceFollowRedirects = true
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            doInput = true
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "vitesse/1.0 (Android)")
          }
          val code = connection.responseCode
          if (code in 300..399) {
            val loc = connection.getHeaderField("Location")
            if (!loc.isNullOrBlank()) {
              url = URL(loc)
              connection.disconnect()
              return@repeat
            }
          }
          return@repeat
        }

        val code = connection!!.responseCode
        val stream = if (code in 200..299) connection.inputStream else connection.errorStream
        val reader = BufferedReader(InputStreamReader(stream))
        val body = reader.use { it.readText() }
        if (code in 200..299) {
          val json = JSONObject(body)
          val eurObj = json.optJSONObject("eur")
          val rate = eurObj?.optDouble("gbp", Double.NaN)
          if (rate != null && !rate.isNaN()) return@withContext rate
        } else {
          Log.w("CurrencyApi", "HTTP $code for $url: $body")
        }
      } catch (e: Exception) {
        Log.w("CurrencyApi", "fetchEurToGbpRate failed for $url", e)
      } finally {
        connection?.disconnect()
      }
    }
    null
  }
}
