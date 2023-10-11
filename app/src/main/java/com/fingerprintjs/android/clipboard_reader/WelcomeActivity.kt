package com.fingerprintjs.android.clipboard_reader


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.ClipboardHistoryActivity


class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val startButton = findViewById<View>(R.id.get_started_button)
        startButton.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                showRequestPermissionDialog {
                    openScreenWithRequestPermissionIfNeeded()
                }
            }
            else openScreenWithRequestPermissionIfNeeded()
        }

        val termsButton = findViewById<TextView>(R.id.terms_btn)

        val spannableTermsBtnText = SpannableString(this.getString(R.string.agreement))
        spannableTermsBtnText.setSpan(UnderlineSpan(), 39, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        termsButton.text = spannableTermsBtnText
        termsButton.setOnClickListener { openScreen(TermsActivity::class.java) }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                openScreenWithRequestPermissionIfNeeded()
            } else {
                openScreen(ClipboardHistoryActivity::class.java)
            }
        }
    }

    private fun showRequestPermissionDialog(listener: () -> (Unit)) {
        AlertDialog
            .Builder(this)
            .setTitle("Permission is required")
            .setMessage("Draw over other apps permission is required to launch the demo")
            .setPositiveButton("Got it")  {
                    dialog, which ->
                dialog.dismiss()
                listener.invoke()
            }
            .show()
    }

    private fun openScreenWithRequestPermissionIfNeeded() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            return
        }
        openScreen(ClipboardHistoryActivity::class.java)
    }

    private fun openScreen(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}

private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323
