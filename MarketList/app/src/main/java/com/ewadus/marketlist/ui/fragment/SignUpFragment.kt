package com.ewadus.marketlist.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ewadus.marketlist.R
import com.ewadus.marketlist.databinding.FragmentSignUpBinding
import com.ewadus.marketlist.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.btnRegis.setOnClickListener {
            emailSingUp()
        }

        return binding.root
    }

    private fun emailSingUp() {
        val inputFullName = binding.edtFullname.text.toString()
        val inputEmail = binding.edtEmail.text.toString()
        val inputPWD = binding.edtPassword.text.toString()
        val inputCFPWD = binding.edtCfPassword.text.toString()

        if (inputFullName.isNotEmpty() && inputEmail.isNotEmpty() && inputPWD.isNotEmpty() && inputCFPWD.isNotEmpty()) {
            if (inputPWD == inputCFPWD) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.createUserWithEmailAndPassword(
                            inputEmail, inputCFPWD
                        ).await()
                        withContext(Dispatchers.Main) {
                            checkUserState()
                            Snackbar.make(
                                binding.root,
                                "Registration is complete",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                e.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {

                    try {
                        val uerID = auth.currentUser!!.uid
                        val documentRef = firestore.collection("users").document(uerID)
                        val mapUserInfo = mutableMapOf<String, Any>()
                        mapUserInfo["email"] = inputEmail
                        mapUserInfo["full_name"] = inputFullName
                        documentRef.set(mapUserInfo).await()
                        withContext(Dispatchers.Main) {
                            Snackbar.make(
                                binding.root,
                                "Registration is complete",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Snackbar.make(binding.root, e.message.toString(), Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }
                }

            } else {
                Snackbar.make(binding.root, "Please check your password", Snackbar.LENGTH_LONG)
                    .show()
                binding.edtCfPassword.error = "Something wrong"
                binding.edtPassword.error = "Something wrong"
            }
        } else {
            binding.edtEmail.error = "Please fill out"
            binding.edtFullname.error = "Please fill out"
            binding.edtPassword.error = "Please fill out"
            binding.edtCfPassword.error = "Please fill out"

        }
    }

    private fun checkUserState() {

        if (auth.currentUser != null) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}