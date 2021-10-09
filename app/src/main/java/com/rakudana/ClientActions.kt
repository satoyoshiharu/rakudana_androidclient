package com.rakudana

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.Intent.CATEGORY_LAUNCHER
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import java.util.jar.Attributes


class ClientActions : AppCompatActivity() {

    val DEBUG = false//true

    private fun maintainContacts(contactName: String, number: String)
    {
        // update call records
        val preferences = applicationContext.getSharedPreferences("rakudana", Context.MODE_PRIVATE)
        var callrecords: String? = preferences.getString("callrecs", "")
        if (callrecords?.indexOf("$contactName:$number") == -1) {
            if (DEBUG) Log.d("ClientActions", "new record: $contactName:$number")
            val crCount = callrecords.split(",")
            if (crCount.size > 10) {// drop 1st record
                val firstrecEnd = callrecords.indexOf(',', 1)
                callrecords = callrecords.substring(firstrecEnd)
            }
            val editor = preferences.edit()
            if (callrecords.length == 0)
                callrecords = contactName + ':' + number
            else
                callrecords = callrecords + "," + contactName + ':' + number
            editor.putString("callrecs", callrecords)
            if (DEBUG) Log.d("ClientActions", "callrecords updated: $callrecords")
            editor.apply()
        } else {
            if (DEBUG) Log.d("ClientActions", "$contactName:$number is found in $callrecords")
        }
    }

    private fun handleApplink() {
        val appLinkIntent = intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data
        val context = applicationContext

        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            if (DEBUG) Log.d("ClientActions", "AppLinkData: $appLinkData")
            val intentID = appLinkData.lastPathSegment
            if (DEBUG) Log.d("ClientActions", "intentID: $intentID")
            when (intentID) {
                "dial" -> {
                    val intent = Intent(Intent.ACTION_PICK).apply {
                        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
                "make_call" -> {
                    val intent = Intent(Intent.ACTION_PICK).apply {
                        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                    }

                    val contactName = appLinkData.getQueryParameter("contact")
                    if (DEBUG) Log.d("ClientActions", "contact name: $contactName")

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
                                    var number = cursor.getString(numberIndex)
                                    number = number.replace("-", "")
                                    if (DEBUG) Log.d("ClientActions", "number: $number")

                                    if (contactName !== null) maintainContacts(contactName, number)

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

                    val contactName = appLinkData.getQueryParameter("contact")
                    if (DEBUG) Log.d("ClientActions", "contact name: $contactName")

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
                                    if (DEBUG) Log.d("ClientActions", "number: $number")
                                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        putExtra("sms_body", message)
                                    }

                                    if (contactName !== null) maintainContacts(contactName, number)

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
                "save_memo" -> {
                    val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                        finish()
                    }
                }
                "put_page_shortcut" -> {
                    val intentIntegrator = IntentIntegrator(this) // SDK24+
                    val zxingActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                        val intentResult = IntentIntegrator.parseActivityResult(it.resultCode, it.data)
                        if (intentResult.contents != null) {
                            Toast.makeText(this, intentResult.contents, Toast.LENGTH_LONG).show()
                            val url = intentResult.getContents()
                            val label = url

                            val launchIntent = Intent(Intent.ACTION_VIEW)
                            launchIntent.data = Uri.parse(url)
                            val manager = getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager //SDK26+
                            val info = ShortcutInfo.Builder(this, url)
                                    .setShortLabel(label)
                                    .setIcon(Icon.createWithResource(this, R.mipmap.rakudana_launcher_round))
                                    .setIntent(launchIntent)
                                    .build()
                            manager.requestPinShortcut(info, null)
                        }
                    }
                    zxingActivityResultLauncher.launch(intentIntegrator.createScanIntent())
                }
                "map" -> {
                    val query = appLinkData.getQueryParameter("q")
                    val gmmIntentUri = Uri.parse("geo:0,0?q=$query")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    mapIntent.resolveActivity(packageManager)?.let {
                        startActivity(mapIntent)
                    }
                }
                /*"news" -> {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(CATEGORY_LAUNCHER)
                    intent.setPackage("jp.gocro.smartnews.android")
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                            putExtra(SearchManager.QUERY, "スマートニュース")
                        }
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        }
                    }
                }*/
                /*
                "search" -> {
                    val query = appLinkData.getQueryParameter("query")
                    val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                        putExtra(SearchManager.QUERY, query)
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
                else -> {
                    val query = appLinkData.getQueryParameter("query")
                    val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                        putExtra(SearchManager.QUERY, query)
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
                */
            }
            // Invoke CALL system intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (DEBUG) Log.d("ClientActions", "OnCreate")
        handleApplink()
    }

    override fun onRestart() {
        if (DEBUG) Log.d("RecordTransferActivity", "onRestart")
        super.onRestart()
    }

    override fun onResume() {
        if (DEBUG) Log.d("RecordTransferActivity", "OnResume")
        super.onResume()
    }

    override fun onStop() {
        if (DEBUG) Log.d("RecordTransferActivity", "OnStop")
        super.onStop()
    }

    override fun onDestroy() {
        if (DEBUG) Log.d("RecordTransferActivity", "OnDestroy")
        super.onDestroy()
    }

}

