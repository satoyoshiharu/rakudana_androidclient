package com.rakudana

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
//import com.android.volley.Response
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
import java.lang.reflect.Method
import java.net.URI
import java.nio.charset.Charset

class ClientActions : AppCompatActivity() {

    val REQUEST_SELECT_PHONE_NUMBER = 1

    fun handle_appLink(intent: Intent) {
        val appLinkIntent = intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data
        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            Log.d("ClientActions", "AppLinkData: $appLinkData")
            val intentID = appLinkData.lastPathSegment
            Log.d("ClientActions", "intentID: $intentID")
            when (intentID) {
                "make_call" -> {
                    var intent = Intent(Intent.ACTION_PICK).apply {
                        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                    }
                    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            val data: Intent = result.data!!
                            val contactUri: Uri = data.data!!
                            val projection: kotlin.Array<kotlin.String> =
                                    kotlin.arrayOf(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
                            contentResolver.query(contactUri, projection, null, null, null).use { cursor ->
                                // If the cursor returned is valid, get the phone number
                                if (cursor!!.moveToFirst()) {
                                    val numberIndex = cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    val number = cursor.getString(numberIndex)
                                    Log.d("ClientActions", "number: $number")
                                    val intent = android.content.Intent(android.content.Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:$number")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                    resultLauncher.launch(intent)
                }
                "send_short_message" -> {
                    val message = appLinkData.getQueryParameter("text")
                    var intent = Intent(Intent.ACTION_PICK).apply {
                        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                    }
                    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            val data: Intent = result.data!!
                            val contactUri: Uri = data.data!!
                            val projection: kotlin.Array<kotlin.String> =
                                    kotlin.arrayOf(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
                            contentResolver.query(contactUri, projection, null, null, null).use { cursor ->
                                // If the cursor returned is valid, get the phone number
                                if (cursor!!.moveToFirst()) {
                                    val numberIndex = cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    val number = cursor.getString(numberIndex)
                                    Log.d("ClientActions", "number: $number")
                                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        putExtra("sms_body", message)
                                    }
                                    intent.data = Uri.parse("sms:$number")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                    resultLauncher.launch(intent)
                }
                /*
                "send_data" -> {
                    val port = appLinkData.getQueryParameter("port")
                    //val url = URI("wss://rakudana.com:" + port + "/ws/a")
                    val url = URI("ws://192.168.0.19:"+port + "/ws/a")
                    Log.d("ClientActions", "connect to url: $url")
                    val wsClient = WebSocketClient(this , url )
                    wsClient.connect()
                    while (true) {
                        if (wsClient.isOpen) {
                            wsClient.send("hello")
                            Log.d("ClientActions", "sent")
                            break
                        }
                    }
                    Log.d("ClientActions", "ws closing")
                    wsClient.close()

                    //reference https://riis.com/blog/sending-requests-using-android-volley/
                    val queue = Volley.newRequestQueue(this)
                    val url = "http://192.168.0.19:"+port + "/post"
                    Log.d("ClientActions", "connect to url: $url")
                    val requestBody = "hello"
                    val stringReq : StringRequest =
                            object : StringRequest(Method.POST, url,
                                    Response.Listener { response ->
                                        var strResp = response.toString()
                                        Log.d("API", strResp)
                                    },
                                    Response.ErrorListener { error ->
                                        Log.d("API", "error => $error")
                                    }
                            ){
                                override fun getBody(): ByteArray {
                                    return requestBody.toByteArray(Charset.defaultCharset())
                                }
                            }
                    queue.add(stringReq)

                }
                 */
                else -> {
                }
            }
            // Invoke CALL system intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_client_actions)
        Log.d("ClientActions", "OnCreate")
        //handle_appLink(intent)
    }

    override fun onRestart() {
        Log.d("RecordTransferActivity", "onRestart")
        super.onRestart()
    }

    override fun onResume() {
        Log.d("RecordTransferActivity", "OnResume")
        super.onResume()
        handle_appLink(intent)
    }

    override fun onStop() {
        Log.d("RecordTransferActivity", "OnStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("RecordTransferActivity", "OnDestroy")
        super.onDestroy()
    }

}

