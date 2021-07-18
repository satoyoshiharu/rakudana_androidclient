package com.rakudana

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity

class ClientActions2 : AppCompatActivity() {

    private fun handleApplink() {
        val appLinkIntent = intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data
        Log.d("ClientActions", "appLinkIntent: $appLinkIntent, appLinkAction: $appLinkAction, appLinkData: $appLinkData")

        if (appLinkAction == Intent.ACTION_VIEW && appLinkData != null) {
            Log.d("ClientActions", "AppLinkData: $appLinkData")
            val intentID = appLinkData.lastPathSegment
            Log.d("ClientActions", "intentID: $intentID")

            when (intentID) {
                "make_call" -> {
                    //val contactName = appLinkData.getQueryParameter("contact") //???
                    //Log.d("ClientActions", "contact name: $contactName")

                    val pickIntent = Intent(Intent.ACTION_PICK).apply {
                        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                    }
                    Log.d("ClientActions", "make_call, pickIntent: $pickIntent")

                    val resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
                        Log.d("ClientActions", "resultLauncher...")
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

                                    /*+++ record a pair of contact name and tel number ???
                                    if (contactName !== null) {
                                        val preferences = getSharedPreferences("rakudana", MODE_PRIVATE)
                                        var callrecords: String? = preferences.getString("callrec","")
                                        Log.d("ClientActions", "call record->:"+contactName+":"+number)
                                        val editor = preferences.edit()
                                        callrecords += "," + contactName + ':' + number
                                        editor.putString("callrec", callrecords)
                                        editor.apply()
                                        Log.d("ClientActions", "callrec->"+callrecords)
                                    }
                                    */

                                    val dialIntent = Intent(Intent.ACTION_DIAL)
                                    dialIntent.data = Uri.parse("tel:$number")
                                    if (dialIntent.resolveActivity(packageManager) != null) {
                                        startActivity(dialIntent)
                                    }
                                }
                            }
                        }
                    }

                    Log.d("ClientActions", "resultLauncher: $resultLauncher")
                    resultLauncher.launch(pickIntent)
                }
                "send_short_message" -> {
                    val message = appLinkData.getQueryParameter("text")
                    val sendIntent = Intent(Intent.ACTION_PICK).apply {
                        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                    }
                    val resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
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

                                    val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        putExtra("sms_body", message)
                                    }
                                    sendIntent.data = Uri.parse("sms:$number")
                                    if (sendIntent.resolveActivity(packageManager) != null) {
                                        startActivity(sendIntent)
                                    }
                                }
                            }
                        }
                    }
                    resultLauncher.launch(sendIntent)
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
        Log.d("ClientActions", "OnCreate")
    }

    override fun onRestart() {
        Log.d("RecordTransferActivity", "onRestart")
        super.onRestart()
    }

    override fun onResume() {
        Log.d("RecordTransferActivity", "OnResume")
        super.onResume()
        handleApplink()
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

