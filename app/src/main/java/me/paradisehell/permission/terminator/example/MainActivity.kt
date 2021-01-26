package me.paradisehell.permission.terminator.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import me.paradisehell.permission.terminator.PermissionTerminator

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.button_audio).setOnClickListener {
            PermissionTerminator
                .with(this)
                .permissions(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES
                )
                .request(
                    onGranted = {
                        Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                    },
                    onDenied = { _, _ ->
                        Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
                    },
                    onNeverAsked = { _, _, _ ->
                        Toast.makeText(this, "NeverAsk", Toast.LENGTH_SHORT).show()
                    }
                )
        }
    }
}
