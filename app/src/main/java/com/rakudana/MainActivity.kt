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

    val DEBUG = false

    private fun main() {
        // kick a background thread UploadWorker with web socket port number
        var port = (49152..65535).random()
        if (DEBUG) Log.d("MainActivity", "port: $port")
        val uploadWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<UploadWorker>()
                        .setInputData(workDataOf("PORT" to port))
                        .setInitialDelay(1000, TimeUnit.MILLISECONDS)
                        .setBackoffCriteria(BackoffPolicy.LINEAR, 1000, TimeUnit.MILLISECONDS)
                        .addTag("upload")
                        .build()
        val workManager = WorkManager.getInstance(this)
        workManager.cancelAllWork()//cancelAllWorksByTag("upload")
        workManager.enqueue(uploadWorkRequest)
        // invoke browser
        //val url = "http://192.168.0.19:8080/www/index.html?invoker=rakudana_app&port=" + port.toString()
        val url = "https://rakudana.com:8080/www/index.html?invoker=rakudana_app&port=" + port.toString()
        val uri = Uri.parse(url)
        if (DEBUG) Log.d("MainActivity", "invoke url: $url")
        val browserIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        }
        startActivity(browserIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (DEBUG) Log.d("MainActivity", "onCreate")
        if (DEBUG) Log.d("MainActivity", "savedInstanceState: $savedInstanceState")
        super.onCreate(savedInstanceState)
        // condition of trigger by user. Otherwise AppLink invokation for ClientActions features will trigger main and hide ClientAction thread.
        if (savedInstanceState==null) main()
        finish()
    }

    override fun onPause() {
        if (DEBUG) Log.d("MainActivity", "onPause")
        super.onPause()
    }

    override fun onRestart() {
        if (DEBUG) Log.d("MainActivity", "onRestart")
        super.onRestart()
    }

    override fun onResume() {
        if (DEBUG) Log.d("MainActivity", "OnResume")
        super.onResume()
        //main()
    }

    override fun onStop() {
        if (DEBUG) Log.d("MainActivity", "OnStop")
        super.onStop()
        onDestroy()
    }

    override fun onDestroy() {
        if (DEBUG) Log.d("MainActivity", "OnDestroy")
        super.onDestroy()
    }
}

class UploadWorker(appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams) {

    val DEBUG = false

    override fun doWork(): Result {

        val port = inputData.getInt("PORT", 0) ?: return Result.failure()
        val portString = port.toString()
        if (DEBUG) Log.d("UploadWorker", "port: $inputData")
        //val url = "ws://192.168.0.19:$portString/ws/a"
        val url = "wss://rakudana.com:$portString/ws/a"

        // Reference: https://www.javadoc.io/doc/org.java-websocket/Java-WebSocket/1.3.4/org/java_websocket/client/WebSocketClient.html
        // Bug information: https://github.com/alexandrainst/processing_websockets/issues/6
        // If server is not ready yet, connection is refused.
        // Then the .connect function handles exception in itself, and can't catch it outside.

        try {
            while (true) {
                val wsClient = WebSocketClient(this, URI(url))
                if (DEBUG) Log.d("UploadWorker", "connecting to url: $url")
                if (wsClient.connectBlocking()) {
                    while (!(wsClient.isOpen())) {
                        if (DEBUG) Log.d("UploadWorker", "web socket $portString is not open")
                        Thread.sleep(100)
                    }
                    if (DEBUG) Log.d("UploadWorker", "web socket open")

                    val preferences =  applicationContext.getSharedPreferences(
                        "rakudana",
                        Context.MODE_PRIVATE
                    )
                    val contacts: String? = preferences.getString("contacts", "")
                    if (DEBUG) Log.d("ClientActions", "contacts: $contacts")
                    val callrecords: String? = preferences.getString("callrecs", "")
                    if (DEBUG) Log.d("ClientActions", "call records: $callrecords")

                    wsClient.send(contacts + "\n" + callrecords)
                    if (DEBUG) Log.d("UploadWorker", "sent to $portString")
                    break
                } else {
                    if (DEBUG) Log.d("UploadWorker", "web socket $portString connection failed")
                    wsClient.close()
                    Thread.sleep(1000)
                }
            }
            // todo: keep open web socket to receive data
            //Log.d("UploadWorker", "ws closing")
            //wsClient.close()
            // Indicate whether the work finished successfully with the Result
            return Result.success()
        } catch (throwable: Throwable) {
            if (DEBUG) Log.d("UploadWorker", "exception")
            return Result.failure()
        }
    }
}



