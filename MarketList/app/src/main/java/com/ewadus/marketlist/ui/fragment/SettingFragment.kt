package com.ewadus.marketlist.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ewadus.marketlist.databinding.FragmentSettingBinding
import com.ewadus.marketlist.ui.RegisterActivity
import com.google.firebase.auth.FirebaseAuth


class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.btnSave.setOnClickListener {
            saveInfo()
        }

        binding.btnLogout.setOnClickListener {
            signOut()
        }
        return binding.root
    }

    private fun saveInfo() {

        val fullName = binding.edtFullname.text.toString()
        val email = binding.edtEmail.text.toString()
        val oldPassword = binding.edtOldPwd.text.toString()
        val newPassword = binding.edtNewPwd.text.toString()
        val imgProfile = binding.imgProfile
        val infoModel = mutableMapOf<String,Any>()
           infoModel["full_name"] = fullName
           infoModel["email"] = email
           infoModel[""]



    }

    private fun signOut() {
        val intent = Intent(requireContext(), RegisterActivity::class.java)
        auth.signOut()
        startActivity(intent)
        Toast.makeText(requireContext(),"Signed out",Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}