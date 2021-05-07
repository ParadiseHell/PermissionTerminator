package org.paradisehell.middleware.permission.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.paradisehell.middleware.permission.PermissionCallback
import org.paradisehell.middleware.permission.PermissionTerminator

class MainActivity : AppCompatActivity(), View.OnClickListener,
    PermissionCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.button_normal_permission).setOnClickListener(this)
        findViewById<View>(R.id.button_access_notification_policy).setOnClickListener(this)
        findViewById<View>(R.id.button_manage_external_storage).setOnClickListener(this)
        findViewById<View>(R.id.button_request_install_packages).setOnClickListener(this)
        findViewById<View>(R.id.button_system_alert_window).setOnClickListener(this)
        findViewById<View>(R.id.button_write_settings).setOnClickListener(this)
        findViewById<View>(R.id.button_all).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_normal_permission -> {
                PermissionTerminator
                    .with(this)
                    .permissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_CALENDAR
                    )
                    .request(this)
            }
            R.id.button_access_notification_policy -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PermissionTerminator
                        .with(this)
                        .permissions(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                        .request(this)
                }
            }
            R.id.button_manage_external_storage -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    PermissionTerminator
                        .with(this)
                        .permissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                        .request(this)
                }
            }
            R.id.button_request_install_packages -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PermissionTerminator
                        .with(this)
                        .permissions(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                        .request(this)
                }
            }
            R.id.button_system_alert_window -> {
                PermissionTerminator
                    .with(this)
                    .permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    .request(this)
            }
            R.id.button_write_settings -> {
                PermissionTerminator
                    .with(this)
                    .permissions(Manifest.permission.WRITE_SETTINGS)
                    .request(this)
            }
            R.id.button_all -> {
                val permissionList = mutableListOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_SETTINGS
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    permissionList.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    permissionList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    permissionList.add(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                }
                PermissionTerminator
                    .with(this)
                    .permissions(permissionList)
                    .request(this)
            }
        }
    }

    override fun onGranted(grantedPermissionList: List<String>) {
        Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
    }

    override fun onDenied(grantedPermissionList: List<String>, deniedPermissionList: List<String>) {
        Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
    }

    override fun onNeverAsked(
        grantedPermissionList: List<String>,
        deniedPermissionList: List<String>,
        neverAskPermissionList: List<String>
    ) {
        Toast.makeText(this, "NeverAsk", Toast.LENGTH_SHORT).show()
    }
}
