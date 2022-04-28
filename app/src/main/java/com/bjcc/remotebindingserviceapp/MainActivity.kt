package com.bjcc.remotebindingserviceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bjcc.remotebindingserviceapp.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var myServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Timber.d("onCreate, Thread id: ${Thread.currentThread().id}")

        myServiceIntent = Intent(this, MyService::class.java)

        binding.btnStartService.setOnClickListener {
            Timber.d("Service Started, Thread id: ${Thread.currentThread().id}")

            startService(myServiceIntent)

            Toast.makeText(this@MainActivity, "Service Started", Toast.LENGTH_SHORT).show()
        }

        binding.btnStopService.setOnClickListener {
            Timber.d("Service Stopped, Thread id: ${Thread.currentThread().id}")

            stopService(myServiceIntent)


            Toast.makeText(this@MainActivity, "Service Stopped", Toast.LENGTH_SHORT).show()
        }
    }

}