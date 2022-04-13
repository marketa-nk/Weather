package com.mint.weather.network

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.mint.weather.R
import com.mint.weather.data.QueryInterceptor
import com.mint.weather.network.googlemaps.GoogleMapsApi
import com.mint.weather.network.openweather.OpenWeatherApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class NetworkService @Inject constructor(context: Context) {

    val googleMapsApi: GoogleMapsApi = getApi(GoogleMapsApi::class.java, "https://maps.googleapis.com/maps/api/geocode/", QueryInterceptor("key", context.resources.getString(R.string.api_key)))
    val openWeatherApi: OpenWeatherApi = getApi(OpenWeatherApi::class.java, "https://api.openweathermap.org/", QueryInterceptor("appid", "b7044fa387aaefecbb6a8888f3624867"))

    private fun <T> getApi(clazz: Class<T>, url: String, vararg interceptors: Interceptor): T {

        val httpClient = getOkHttpClient()
            .also { builder ->
                interceptors.forEach {
                    builder.addInterceptor(it)
                }
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(clazz)
    }

    fun getOkHttpClient(): OkHttpClient.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            OkHttpClient().newBuilder()
        } else {
            getUnsafeOkHttpClient()
        }
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname, session -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}