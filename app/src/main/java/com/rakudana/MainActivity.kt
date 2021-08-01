package com.rakudana

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import java.net.URI
import java.util.concurrent.TimeUnit
import java.util.prefs.Preferences

class MainActivity : AppCompatActivity() {

    private fun main() {
        // kick a background thread UploadWorker with web socket port number
        var port = (49152..65535).random()
        Log.d("MainActivity", "port: $port")
        val uploadWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<UploadWorker>()
                        .setInputData(workDataOf("PORT" to port))
                        .setInitialDelay(3000, TimeUnit.MILLISECONDS)
                        .setBackoffCriteria( BackoffPolicy.LINEAR, 1000, TimeUnit.MILLISECONDS)
                        .addTag("upload")
                        .build()
        val workManager = WorkManager.getInstance(this)
        workManager.cancelAllWorkByTag("upload")
        workManager.enqueue(uploadWorkRequest)
        // invoke browser
        //val url = "http://192.168.0.19:8080/www/index.html?invoker=rakudana_app&port=" + port.toString()
        val url = "https://rakudana.com:8080/www/index.html?invoker=rakudana_app&port=" + port.toString()
        val uri = Uri.parse(url)
        Log.d("MainActivity", "invoke url: $url")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate")
        Log.d("MainActivity", "savedInstanceState: $savedInstanceState")
        super.onCreate(savedInstanceState)
        if (savedInstanceState==null) main()
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

        val port = inputData.getInt("PORT", 0 ) ?: return Result.failure()
        val portString = port.toString()
        Log.d("UploadWorker", "port: $inputData")
        //val url = "ws://192.168.0.19:$portString/ws/a"
        val url = "wss://rakudana.com:$portString/ws/a"
        try {
            val wsClient = WebSocketClient(this, URI(url))
            Log.d("UploadWorker", "connecting to url: $url")
            wsClient.connectBlocking()
            while (true) {
                Thread.sleep(1000)
                if (wsClient.isOpen) {
                    Log.d("UploadWorker", "web socket open")

                    val preferences =  applicationContext.getSharedPreferences("rakudana", Context.MODE_PRIVATE)
                    val contacts: String? = preferences.getString("contacts","")
                    Log.d("ClientActions", "contacts: $contacts")
                    val callrecords: String? = preferences.getString("callrecs","")
                    Log.d("ClientActions", "call records: $callrecords")

                    wsClient.send(contacts + "\n" + callrecords)
                    Log.d("UploadWorker", "sent to $portString")
                    break
                } else {
                    Log.d("UploadWorker", "web socket $portString is not open")
                }
            }
            // todo: keep open web socket to receive data
            //Log.d("UploadWorker", "ws closing")
            //wsClient.close()
            // Indicate whether the work finished successfully with the Result
            return Result.success()
        } catch (throwable: Throwable) {
            Log.d("UploadWorker", "exception")
            return Result.failure()
        }
    }
}



