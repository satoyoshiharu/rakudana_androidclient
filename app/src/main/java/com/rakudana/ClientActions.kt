package com.rakudana

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

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
        handle_appLink(intent)
        /*
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
                    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result->
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
                    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result->
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
                else -> {
                }
            }
            // Invoke CALL system intent
        }
        */
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

