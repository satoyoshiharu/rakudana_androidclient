package com.rakudana

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ClientActions : AppCompatActivity() {

    private fun handleApplink() {
        val appLinkIntent = intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data
        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            Log.d("ClientActions", "AppLinkData: $appLinkData")
            val intentID = appLinkData.lastPathSegment
            Log.d("ClientActions", "intentID: $intentID")
            when (intentID) {
                "make_call" -> {
                    val intent = Intent(Intent.ACTION_PICK).apply {
                        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                    }
                    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            val data: Intent = result.data!!
                            val contactUri: Uri = data.data!!
                            val projection: Array<String> =
                                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            contentResolver.query(contactUri, projection, null, null, null).use { cursor ->
                                // If the cursor returned is valid, get the phone number
                                if (cursor!!.moveToFirst()) {
                                    val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
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
                    val intent = Intent(Intent.ACTION_PICK).apply {
                        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                    }
                    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            val data: Intent = result.data!!
                            val contactUri: Uri = data.data!!
                            val projection: Array<String> =
                                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            contentResolver.query(contactUri, projection, null, null, null).use { cursor ->
                                // If the cursor returned is valid, get the phone number
                                if (cursor!!.moveToFirst()) {
                                    val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
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
        Log.d("ClientActions", "OnCreate")
        handleApplink()
    }

    override fun onRestart() {
        Log.d("RecordTransferActivity", "onRestart")
        super.onRestart()
    }

    override fun onResume() {
        Log.d("RecordTransferActivity", "OnResume")
        super.onResume()
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

