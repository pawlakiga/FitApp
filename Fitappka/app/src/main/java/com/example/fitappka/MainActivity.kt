package com.example.fitappka

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.example.fitappka.bluetooth.BluetoothLeService.LocalBinder
import com.example.fitappka.bluetooth.BlunoLibrary
import com.example.fitappka.database.FitappkaDatabase
import com.example.fitappka.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        var database: FitappkaDatabase? = null
    }

    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Getting binding class from layout
        @Suppress("UNUSED_VARIABLE")
        database = FitappkaDatabase.getInstance(this)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }
}
