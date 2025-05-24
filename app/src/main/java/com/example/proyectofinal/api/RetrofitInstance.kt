package com.example.proyectofinal.api
import android.content.Context
import com.example.proyectofinal.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object RetrofitInstance {

    val BASE_URL = ConfigReader.getProperty("BASE_URL")
    //private const val BASE_URL = "http:/192.168.0.55:8080/" // 10.0.2.2 192.168.76.87 192.168.0.55
    fun getRetrofit(context: Context): Retrofit {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val inputStream: InputStream = context.resources.openRawResource(R.raw.tuapp) // tu certificado
        val certificate = certificateFactory.generateCertificate(inputStream)
        inputStream.close()

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", certificate)

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)

        val trustManagers = trustManagerFactory.trustManagers
        val trustManager = trustManagers[0] as X509TrustManager

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(trustManager), null)

        val okHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}