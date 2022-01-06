package com.ewadus.marketlist.ui.fragment

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ewadus.marketlist.R
import com.ewadus.marketlist.databinding.FragmentSignInBinding
import com.ewadus.marketlist.ui.MainActivity
import com.ewadus.marketlist.util.Constants.GOOGLE_LOGIN_REQUEST_CODE
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception


class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        checkUserState()


        binding.btnLogin.setOnClickListener {
            emailLogIn()
        }

        binding.imgGoogle.setOnClickListener {
            googleLogIn()

        }


        binding.imgLine.setOnClickListener {
        }

        return binding.root
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN_REQUEST_CODE) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthenFirebase(it)
            }
        }

    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {

        Log.d("Facebook", "handleFacebookAccessToken:$accessToken")
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credential).await()

                withContext(Dispatchers.Main) {
                    checkUserState()
                       auth.currentUser.toString()
                    Toast.makeText(requireContext(),auth.currentUser.toString(),Toast.LENGTH_LONG).show()
                }

            }catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(),e.message.toString(),Toast.LENGTH_LONG).show()
                }

            }
        }

    }

    private fun googleLogIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val signInClient = GoogleSignIn.getClient(requireContext(), gso)
        signInClient.signInIntent.also {
            startActivityForResult(it, GOOGLE_LOGIN_REQUEST_CODE)
        }
    }



    private fun googleAuthenFirebase(googleSignInAccount: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credential).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Log in Successfully", Toast.LENGTH_LONG)
                        .show()
                    checkUserState()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.root, e.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun emailLogIn() {
        val inputEmail = binding.edtUsername.text.toString()
        val inputPWD = binding.edtPassword.text.toString()

        if (inputEmail.isNotEmpty() && inputPWD.isNotEmpty()) {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(inputEmail, inputPWD).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Log in Successfully", Toast.LENGTH_LONG)
                            .show()
                        checkUserState()
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(binding.root, e.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }

                }
            }
        }
    }

    private fun checkUserState() {
        if (auth.currentUser != null) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}