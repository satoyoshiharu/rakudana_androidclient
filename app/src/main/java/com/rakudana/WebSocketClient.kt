package com.rakudana

import android.content.Context
import android.net.Uri
import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

//import java.net.URI


class WebSocketClient(val activity: UploadWorker, uri: URI) : WebSocketClient(uri) {

    val DEBUG = false

    override fun onOpen(handshakedata: ServerHandshake?) {
        if (DEBUG) Log.d("UploadWorker", "WSサーバに接続しました。")
        if (DEBUG) Log.d("UploadWorker", "スレッド：「${Thread.currentThread().name}」で実行中")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        if (DEBUG) Log.d("UploadWorker", "WSサーバから切断しました。reason:${reason}")
        if (DEBUG) Log.d("UploadWorker", "スレッド：「${Thread.currentThread().name}」で実行中")
    }

    override fun onMessage(message: String?) {
        if (DEBUG) Log.d("UploadWorker", "メッセージを受け取りました。")
        if (DEBUG) Log.d("UploadWorker", "スレッド：「${Thread.currentThread().name}」で実行中")
        /*activity.runOnUiThread {
            Log.i(javaClass.simpleName, "メッセージをTextViewに追記しました。")
            Log.i(javaClass.simpleName, "スレッド：「${Thread.currentThread().name}」で実行中")
        }*/

        if (DEBUG) Log.d("UploadWorker", "受信: $message")
        val preferences = activity.applicationContext.getSharedPreferences("rakudana", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("contacts", message)
        editor.apply()
    }

    override fun send(text: String?) {
        if (DEBUG) Log.d("UploadWorker", "送信: $text")
        super.send(text)
    }

    override fun onError(ex: Exception?) {
        if (DEBUG) Log.d("UploadWorker", "エラーが発生しました。", ex)
        if (DEBUG) Log.d("UploadWorker", "スレッド：「${Thread.currentThread().name}」で実行中")
    }
}