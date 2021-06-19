package com.rakudana

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        val appLinkIntent: Intent = getIntent()
        //String appLinkAction = appLinkIntent.getAction();
        val appLinkData: Uri? = appLinkIntent.getData()
        if (appLinkData != null) Log.d("MainActivity", appLinkData.toString())
        //static final Uri uri = Uri.parse("https://rakuidana.com:8080/www/index.html?invoker=rakudana_app");
        val web: Uri = Uri.parse("https://192.168.0.19:8080/www/index.html?invoker=rakudana_app")
        if (savedInstanceState == null && appLinkData == null) { // invoked from launcher?
            startActivity(Intent(Intent.ACTION_VIEW, web))
            //startActivity(Intent(intent.ACTION_VOICE_COMMAND))

        }
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
        val appLinkIntent = intent
        //String appLinkAction = appLinkIntent.getAction();
        val appLinkData = appLinkIntent.data
        if (appLinkData != null) Log.d("MainActivity", appLinkData.toString())
        //static final Uri uri = Uri.parse("https://rakuidana.com:8080/www/index.html?invoker=rakudana_app");
        val web: Uri = Uri.parse("https://192.168.0.19:8080/www/index.html?invoker=rakudana_app")
        startActivity(Intent(Intent.ACTION_VIEW, web))
        //startActivity(Intent(intent.ACTION_VOICE_COMMAND))
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