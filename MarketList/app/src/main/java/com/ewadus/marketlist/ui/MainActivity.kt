package com.ewadus.marketlist.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.ewadus.marketlist.R
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val navView = findViewById<NavigationView>(R.id.navView)
        val navController = this.findNavController(R.id.nav_host_fragment)
        drawerLayout = findViewById(R.id.drawerLayout)
        NavigationUI.setupWithNavController(navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)


//         val btnLogin = findViewById<Button>(R.id.btn_signIn_login)
//         val btnRegister = findViewById<Button>(R.id.btn_register_login)
//
//
//        btnLogin.setOnClickListener {
//            emailLogin()
//        }
//        btnRegister.setOnClickListener {
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//        }


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

//    private fun emailLogin() {
//        val email = findViewById<TextInputEditText>(R.id.edt_email_login)
//        val password = findViewById<TextInputEditText>(R.id.edt_password_login)
//
//        if (email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
//
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
//                        .await()
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Log in is successful",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main){
//                        Toast.makeText(
//                            this@MainActivity,
//                            e.message.toString(),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//
//                }
//            }
//        }
//
//    }
}