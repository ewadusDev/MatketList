package com.ewadus.marketlist.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ewadus.marketlist.R
import com.ewadus.marketlist.databinding.FragmentSignInBinding
import com.ewadus.marketlist.databinding.FragmentSignUpBinding
import com.ewadus.marketlist.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
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



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener {
            emailLogIn()
        }

       return binding.root
    }

    private fun emailLogIn() {
        val inputEmail = binding.edtUsername.text.toString()
        val inputPWD  = binding.edtPassword.text.toString()

        if(inputEmail.isNotEmpty() && inputPWD.isNotEmpty())  {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(inputEmail,inputPWD).await()
                    withContext(Dispatchers.Main) {
                      Snackbar.make(binding.root,"Login is successful",Snackbar.LENGTH_LONG).show()
                        checkUserState()
                    }


                }catch (e:Exception) {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(binding.root,e.message.toString(),Snackbar.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

    private fun checkUserState() {

        if (auth != null) {
            val intent = Intent(requireContext(),MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}