package com.example.wifitest

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wifitest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        binding.button.setOnClickListener{
            val success = wifiManager.startScan()
            println(success)
            val wifiList = wifiManager.scanResults.map {
                val value = mutableMapOf<String, String>()
                value["SSID"] = it.SSID
                value["BSSID"] = it.BSSID

                value
              }
            println(wifiList)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    private val REQUEST_PERMISSIONS = 1
    // 권한 체크
    private fun checkPermission() {
        val permission = mutableMapOf<String, String>()
        permission["changeWifi"] = Manifest.permission.CHANGE_WIFI_STATE
        permission["accessWifi"] = Manifest.permission.ACCESS_WIFI_STATE
        permission["coarse"] = Manifest.permission.ACCESS_COARSE_LOCATION
        permission["fine"] = Manifest.permission.ACCESS_FINE_LOCATION

        // 현재 권한 상태 검사
        val denied = permission.count { ContextCompat.checkSelfPermission(this, it.value)  == PackageManager.PERMISSION_DENIED }

        if(denied > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = requestPermissions(permission.values.toTypedArray(), REQUEST_PERMISSIONS)
            println(result.toString())
        }
    }

    // 권한 미동의 시 앱 종료
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_PERMISSIONS) {
            grantResults.forEach {
                if(it == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(applicationContext, "서비스의 필요한 권한입니다.\n권한에 동의해주세요.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}