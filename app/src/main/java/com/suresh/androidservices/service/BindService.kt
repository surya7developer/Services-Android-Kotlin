package com.suresh.androidservices.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suresh.androidservices.MainActivity
import kotlin.random.Random

class BindService : Service() {

    private var mIsRandomGeneratorOn: Boolean = false
    private val mBinder = MyServiceBinder()

    companion object {
        private val _mRandomNumber = MutableLiveData<Int>(1)
        private val count: LiveData<Int> get() = _mRandomNumber
        fun getRandomCount() = count
    }

    inner class MyServiceBinder : Binder() {
        fun getService(): BindService = this@BindService
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(MainActivity.LOG_TAG, "In OnBind")
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(MainActivity.LOG_TAG, "In OnReBind")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(
            MainActivity.LOG_TAG,
            "In onStartCommand, thread id: ${Thread.currentThread().id}"
        )
        mIsRandomGeneratorOn = true
        Thread {
            startRandomNumberGenerator()
        }.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mIsRandomGeneratorOn = false
        Log.d(MainActivity.LOG_TAG, "Service Destroyed")
    }

    private fun startRandomNumberGenerator() {
        while (mIsRandomGeneratorOn) {
            try {
                Thread.sleep(1000)
                if (mIsRandomGeneratorOn) {
                    _mRandomNumber.postValue(Random.nextInt(1,100))
                    Log.d(
                        MainActivity.LOG_TAG,
                        "Thread id: ${Thread.currentThread().id}, Random Number: ${_mRandomNumber.value}"
                    )
                }
            } catch (e: InterruptedException) {
                Log.d(MainActivity.LOG_TAG, "Thread Interrupted")
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(MainActivity.LOG_TAG, "In onUnbind")
        return super.onUnbind(intent)
    }
}