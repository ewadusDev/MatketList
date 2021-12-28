package com.ewadus.marketlist.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ewadus.marketlist.R
import com.ewadus.marketlist.databinding.FragmentSettingBinding
import com.google.firebase.auth.FirebaseAuth


class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.btnLogout.setOnClickListener {
            auth.signOut()
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}