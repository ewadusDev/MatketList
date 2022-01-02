package com.ewadus.marketlist.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ewadus.marketlist.data.User
import com.ewadus.marketlist.databinding.FragmentSettingBinding
import com.ewadus.marketlist.ui.RegisterActivity
import com.ewadus.marketlist.util.Constants.IMAGE_REQUEST_CODE
import com.ewadus.marketlist.util.Permissions
import com.ewadus.marketlist.util.Tools
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.URI


class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var imgURI: Uri


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        if (auth.currentUser!!.uid != null) {

            binding.btnSave.setOnClickListener {
                saveInfo()
                bindingData()
            }

            binding.btnLogout.setOnClickListener {
                signOut()
            }

            binding.imgProfile.setOnClickListener {
                pickImage()
            }

        }

        return binding.root
    }

    private fun bindingData() {
        val imgProfile = binding.imgProfile
        val fullName = binding.edtFullname.hint
        val email = binding.edtEmail.hint
        val phone = binding.edtPhone.hint
        val age = binding.edtAge.hint



    }

    private fun pickImage() {

        if (Permissions.hasReadExternalStoragePermission(requireContext())) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        } else {
            Permissions.requestReadExternalStoragePermission(this)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE) {
            try {
                imgURI = data?.data!!

            } catch (e: Exception) {

            }
        }
    }


    private fun saveInfo() {

        val fullName = binding.edtFullname.text.toString()
        val email = binding.edtEmail.text.toString()
        val age = binding.edtAge.text.toString().toInt()
        val phoneNum = binding.edtPhone.text.toString()
        val userModel = User(fullName, email, age, phoneNum, null)
        val currentUser = auth.currentUser?.uid
        val dbRef = firestore.collection("users").document(currentUser!!)

        if (currentUser != null) {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    dbRef.set(userModel).await()
                    Log.i("SettingFragment", "info was saved in fireStore")
                    uploadProfilePic(imgURI)
                    withContext(Dispatchers.Main) {
                        Tools.showToast(requireContext(), "Information is updated")
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Tools.showToast(requireContext(), e.message.toString())
                    }

                }

            }


        }


    }

    private fun uploadProfilePic(imgURI: Uri) {
        val currentUser = auth.currentUser?.uid
        val documentRef = storage.getReference("UserProfilePics/$currentUser.jpg")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                documentRef.putFile(imgURI).await()
                Log.i("SettingFragment", "Img: Image is saved in Storage")


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Tools.showToast(requireContext(), e.message.toString())
                }

            }
        }

    }

    private fun displayProfilePic() {
        val documentRef = storage.getReference("UserProfilePics/" + auth.currentUser!!.uid)

        CoroutineScope(Dispatchers.IO).launch {
            try {


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Tools.showToast(requireContext(), e.message.toString())
                }

            }
        }

    }


    private fun signOut() {
        val intent = Intent(requireContext(), RegisterActivity::class.java)
        auth.signOut()
        startActivity(intent)
        Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}