package com.insightsfromtech.myfoodapp.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.insightsfromtech.myfoodapp.Activities.LoginPages.SigningActivity
import com.insightsfromtech.myfoodapp.R
import com.insightsfromtech.myfoodapp.databinding.ActivityUserProfileBinding
import de.hdodenhof.circleimageview.CircleImageView

class UserProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = getColor(R.color.primary_green)

        val userName = intent.getStringExtra("userName")
        Log.d("userName","$userName")
        val userEmail = intent.getStringExtra("userEmail")
        val userImage = intent.getStringExtra("userImage")
        val bitmapImage = base64ToBitmap(userImage!!)

        findViewById<TextView>(R.id.user_name_text).text = "UserName :$userName"
        findViewById<TextView>(R.id.user_email).text = "Email : $userEmail"
        findViewById<ImageView>(R.id.user_image_circular).setImageBitmap(bitmapImage)
        findViewById<CircleImageView>(R.id.back_arrow).setOnClickListener {
            finish()
        }

        val logoutButton = findViewById<TextView>(R.id.log_out)

        logoutButton.setOnClickListener {
            
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { _, _ ->
                    performLogout()
                }
                .setNegativeButton("No"){dialog,which ->
                    dialog.dismiss()
                }
                .show()

        }



    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SigningActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}