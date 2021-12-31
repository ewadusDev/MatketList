package com.ewadus.marketlist.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ewadus.marketlist.R
import com.ewadus.marketlist.databinding.FragmentFeedbackBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class FeedbackFragment : Fragment() {

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)


        binding.btnSend.setOnClickListener {
            sendData()
        }

        return binding.root
    }

    private fun sendData() {
        val editText = binding.edtFeedback.text.toString()

        val feedbackHashMap = mutableMapOf<String, Any>()
        feedbackHashMap["message"] = binding.edtFeedback.text.toString()

        if (editText.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
            try {
                    fireStore.collection("feedback").add(feedbackHashMap).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Sent message", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }

            }
        }


    }

        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }

    }