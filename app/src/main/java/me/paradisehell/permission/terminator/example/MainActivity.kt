package me.paradisehell.permission.terminator.example

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import me.paradisehell.permission.terminator.PermissionTerminator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.button_audio).setOnClickListener {
            PermissionTerminator
                .with(this)
                .permissions(Manifest.permission.RECORD_AUDIO)
                .request(
                    onGranted = {
                        Toast.makeText(this, "RECORD_AUDIO Granted", Toast.LENGTH_SHORT).show()
                    },
                    onDenied = { _, _ ->
                        Toast.makeText(this, "RECORD_AUDIO Denied", Toast.LENGTH_SHORT).show()
                    },
                    onNeverAsked = { _, _, _ ->
                        Toast.makeText(this, "RECORD_AUDIO NeverAsk", Toast.LENGTH_SHORT).show()
                    }
                )
        }
    }
}
