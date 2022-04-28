package com.bjcc.remotebindingserviceapp

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.widget.Toast
import timber.log.Timber
import java.util.*

private const val MIN = 0
private const val MAX = 100

class MyService : Service() {

    private var randomNumber = 0
    private var isRandomNumberGeneratorOn = false

    inner class RandomNumberRequestHandler : Handler() {
        override fun handleMessage(msg: Message) {

            val messageSendRandomValue = Message.obtain(null, msg.what)

            when (msg.what) {
                RandomValueFlag.GET_RANDOM_NUMBER.value -> {
                    Timber.d("Requesting random number")

                    messageSendRandomValue.arg1 = getRandomNumber()
                }

                RandomValueFlag.GET_RANDOM_COLOR.value -> {
                    Timber.d("Requesting random color")

                    messageSendRandomValue.arg1 = getRandomColor()
                }
            }

            try {
                msg.replyTo.send(messageSendRandomValue)
            } catch (e: RemoteException) {
                Timber.d("" + e.message)
            }

            super.handleMessage(msg)
        }
    }

    private val randomValueMessenger: Messenger = Messenger(RandomNumberRequestHandler())

    override fun onBind(intent: Intent?): IBinder? {
        Timber.d("onBind - Thread id: ${Thread.currentThread().id}")

        return randomValueMessenger.binder
    }

    override fun onRebind(intent: Intent?) {
        Timber.d("onRebind - Thread id: ${Thread.currentThread().id}")

        super.onRebind(intent)
    }
    
    override fun onUnbind(intent: Intent?): Boolean {
        Timber.d("onUnbind - Thread id: ${Thread.currentThread().id}")

        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand - Thread id: ${Thread.currentThread().id}")

        isRandomNumberGeneratorOn = true
        Thread { startRandomNumberGenerator() }.start()

        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopRandomNumberGenerator()
    }

    private fun startRandomNumberGenerator() {
        while (isRandomNumberGeneratorOn) {
            try {
                Thread.sleep(1000)
                if (isRandomNumberGeneratorOn) {
                    randomNumber = Random().nextInt(MAX) + MIN
                    Timber.d("Random Number: $randomNumber, - Thread id: ${Thread.currentThread().id}")
                }
            } catch (e: InterruptedException) {
                Timber.d("Thread Interrupted. - Thread id: ${Thread.currentThread().id}")
            }
        }
    }

    private fun stopRandomNumberGenerator() {
        isRandomNumberGeneratorOn = false
        Toast.makeText(applicationContext, "Service Stopped", Toast.LENGTH_SHORT).show()
    }
    
    private fun getRandomNumber(): Int {
        return randomNumber
    }

    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    enum class RandomValueFlag(val value: Int) {
        GET_RANDOM_NUMBER(0),
        GET_RANDOM_COLOR(1)
    }
}