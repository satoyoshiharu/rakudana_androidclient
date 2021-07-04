package com.rakudana

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import java.net.URI
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    /*
    suspend fun sendData() {
        val url = "ws://192.168.0.19:${this.port}/ws/a"
        Log.d("MainActivity", "connect to url: $url")
        val wsClient = WebSocketClient(this ,  URI(url) )
        wsClient.connect()
        while (true) {
            if (wsClient.isOpen) {
                delay(100L)
                wsClient.send("hello")
                Log.d("ClientActions", "sent")
                break
            }
        }
        Log.d("MainActivity", "ws closing")
        wsClient.close()
    }

    fun main() = runBlocking {
        launch {
            delay(5000L)
            sendData()
        }
        invoke_browser()
    }
    */

    fun main() {

        val port: Int = (49152..65535).random()
        Log.d("MainActivity", "port: $port")

        val myData: Data = workDataOf("PORT_NUMBER" to port.toString())
        val uploadWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<UploadWorker>()
                        .setInputData(myData)
                        .setInitialDelay(3, TimeUnit.SECONDS)
                        .build()
        WorkManager
                .getInstance(this.getApplicationContext())
                .enqueue(uploadWorkRequest)

        val url = "http://192.168.0.19:8080/www/index.html?invoker=rakudana_app&port=$port"
        val uri = Uri.parse(url)
        Log.d("MainActivity", "invoke url: $url")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        //val appLinkIntent = intent
    }

    override fun onPause() {
        Log.d("MainActivity", "onPause")
        super.onPause()
    }

    override fun onRestart() {
        Log.d("MainActivity", "onRestart")
        super.onRestart()
    }

    override fun onResume() {
        Log.d("MainActivity", "OnResume")
        super.onResume()
        main()
    }

    override fun onStop() {
        Log.d("MainActivity", "OnStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("MainActivity", "OnDestroy")
        super.onDestroy()
    }
}

class UploadWorker(appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val port = inputData.getString("PORT_NUMBER") ?: return Result.failure()
        Log.d("UploadWorker", "port: $port")
        val url = "ws://192.168.0.19:$port/ws/a"
        val wsClient = WebSocketClient(this, URI(url))
        Log.d("UploadWorker", "connect to url: $url")
        wsClient.connect()
        while (true) {
            if (wsClient.isOpen) {
                wsClient.send("hello")
                Log.d("UploadWorker", "sent")
                break
            }
        }
        Log.d("UploadWorker", "ws closing")
        wsClient.close()
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

}

