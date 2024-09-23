package com.suresh.androidservices

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.suresh.androidservices.service.BindService.Companion.getRandomCount
import com.suresh.androidservices.databinding.ActivityMainBinding
import com.suresh.androidservices.service.BackgroundService
import com.suresh.androidservices.service.BindService
import com.suresh.androidservices.service.ForegroundService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val managerBindService = ManageBindService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleBackgroundService()
        handleForegroundService()
        handleBindService()

    }

    private fun handleBindService() {
        if (managerBindService.serviceConnection == null) {
            managerBindService.serviceConnection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
                    val service: BindService.MyServiceBinder =
                        iBinder as BindService.MyServiceBinder
                    managerBindService.binder = service.getService()
                    managerBindService.isBindService = true
                    Log.d(LOG_TAG, "onServiceConnected: ")
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    managerBindService.isBindService = false
                    Log.d(LOG_TAG, "onServiceDisconnected: ")
                }
            }
        }

        val binServiceIntent = Intent(this@MainActivity, BindService::class.java)

        binding.apply {

            btnStartService.setOnClickListener {
                Log.d(LOG_TAG, "bind service started")
                startService(binServiceIntent)

            }

            btnStopService.setOnClickListener {
                Log.d(LOG_TAG, "bind service stop")
                stopService(binServiceIntent)
            }

            btnBindService.setOnClickListener {
                managerBindService.serviceConnection?.let {
                    bindService(binServiceIntent, it, Context.BIND_AUTO_CREATE)
                }
                Log.d(LOG_TAG, "bind service")
            }

            btnUnBindService.setOnClickListener {
                if (managerBindService.isBindService) {
                    managerBindService.serviceConnection?.let { unbindService(it) }
                    managerBindService.isBindService = false
                }
                Log.d(LOG_TAG, "Unbind service")
            }

            getRandomCount().observe(this@MainActivity) {
                Log.d(LOG_TAG, "Observer = ${managerBindService.isBindService}")
                if (managerBindService.isBindService) {
                    txtRandomNumber.text = "Random Number : $it"
                } else {
                    txtRandomNumber.text = "Service Not Bound"
                }
            }
        }
    }

    private fun handleForegroundService() {

        binding.apply {
            btnStartForegroundService.setOnClickListener {
                Log.d(LOG_TAG, "handleForegroundService: MainActivity")
                val intent = Intent(this@MainActivity, ForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startService(intent)
                }
            }

            btnStopForegroundService.setOnClickListener {
                Log.d(LOG_TAG, "handleForegroundService: MainActivity")
                val intent = Intent(this@MainActivity, ForegroundService::class.java)
                stopService(intent)
            }
        }
    }

    private fun handleBackgroundService() {
        binding.apply {
            btnStartBackgroundService.setOnClickListener {
                Log.d(LOG_TAG, "handleBackgroundService: MainActivity")
                val intent = Intent(this@MainActivity, BackgroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startService(intent)
                }
            }

            btnStopBackgroundService.setOnClickListener {
                val intent = Intent(this@MainActivity, BackgroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopService(intent)
                }
            }
        }
    }

    companion object {
        val LOG_TAG = "ServiceMainTAGLOG"
    }

    class ManageBindService(
        var serviceConnection: ServiceConnection? = null,
        var binder: BindService? = null,
        var isBindService:Boolean = false
    )
}