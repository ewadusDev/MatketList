package com.ewadus.marketlist.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.ewadus.marketlist.R
import com.ewadus.marketlist.adapter.ViewPagerAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewPager: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_register)

        viewPager = findViewById(R.id.viewpager)
        val pageAdapter = ViewPagerAdapter(this)
        viewPager.adapter = pageAdapter

//        val btnSignUp = findViewById<Button>(R.id.btn_signup_register)
//
//        btnSignUp.setOnClickListener {
//            emailSignUp()
//        }
    }

//    private fun emailSignUp() {
//        val email = findViewById<TextInputEditText>(R.id.edt_email_register)
//        val password = findViewById<TextInputEditText>(R.id.edt_password_register)
//
//        if (email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    auth.createUserWithEmailAndPassword(
//                        email.text.toString(),
//                        password.text.toString()
//                    ).await()
//
//                    withContext(Dispatchers.Main){
//                        Toast.makeText(this@RegisterActivity,"Register is successful",Toast.LENGTH_SHORT).show()
//
//                    }
//
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(this@RegisterActivity,e.message.toString(),Toast.LENGTH_SHORT).show()
//
//                    }
//                }
//            }
//
//        }
//    }
}