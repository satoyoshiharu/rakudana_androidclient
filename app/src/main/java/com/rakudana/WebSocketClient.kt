package com.rakudana

import android.net.Uri
import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

//import java.net.URI


class WebSocketClient(val activity: UploadWorker, uri: URI) : WebSocketClient(uri) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("UploadWorker", "WSサーバに接続しました。")
        Log.d("UploadWorker", "スレッド：「${Thread.currentThread().name}」で実行中")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("UploadWorker", "WSサーバから切断しました。reason:${reason}")
        Log.d("UploadWorker", "スレッド：「${Thread.currentThread().name}」で実行中")
    }

    override fun onMessage(message: String?) {
        Log.d("UploadWorker", "メッセージを受け取りました。")
        Log.d("UploadWorker", "スレッド：「${Thread.currentThread().name}」で実行中")
        /*activity.runOnUiThread {
            Log.i(javaClass.simpleName, "メッセージをTextViewに追記しました。")
            Log.i(javaClass.simpleName, "スレッド：「${Thread.currentThread().name}」で実行中")
        }*/
    }

    override fun send(text: String?) {
        Log.d("UploadWorker", "送信: $text")
        super.send(text)
    }

    override fun onError(ex: Exception?) {
        Log.d("UploadWorker", "エラーが発生しました。", ex)
        Log.d("UploadWorker", "スレッド：「${Thread.currentThread().name}」で実行中")
    }
}