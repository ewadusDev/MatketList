package com.ewadus.marketlist.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ewadus.marketlist.R
import com.ewadus.marketlist.adapter.DetailAdapter
import com.ewadus.marketlist.data.SubItem
import com.ewadus.marketlist.databinding.FragmentDetailBinding
import com.ewadus.marketlist.util.Constants
import com.ewadus.marketlist.util.Constants.IMAGE_REQUEST_CODE
import com.ewadus.marketlist.util.Permissions
import com.ewadus.marketlist.util.Tools
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*


class DetailFragment : Fragment(), DetailAdapter.OnItemClickListener {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var detailAdapter: DetailAdapter
    private val args: DetailFragmentArgs by navArgs<DetailFragmentArgs>()
    private lateinit var imageUri: Uri
    private lateinit var getImageURL: Uri
    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        updateData()

        binding.fabAddSubItem.setOnClickListener {
            createData(requireContext())
        }
        return binding.root
    }

    private fun createData(context: Context) {

        val bottomSheet = BottomSheetDialog(context)
        bottomSheet.setContentView(R.layout.dialog_create_add_sub_item)

        val btnSave = bottomSheet.findViewById<TextView>(R.id.btn_dialog_sub_save)
        val btnCancel = bottomSheet.findViewById<TextView>(R.id.btn_dialog_sub_cancel)
        val imgCover = bottomSheet.findViewById<ImageView>(R.id.img_item_sub_thumbnail)
        val edtItemText = bottomSheet.findViewById<EditText>(R.id.edt_dialog_sub_input)
        val currentTime = System.currentTimeMillis()
        val collectionRef = fireStore.collection("subMainItem")

        btnSave?.setOnClickListener {
            if (edtItemText?.text?.isNotEmpty() == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val subItemModel = SubItem(
                            edtItemText.text.toString(),
                            currentTime.toString(),
                            currentTime.toString(),
                            0,
                            args.mainItemDocRef,
                            getImageURL.toString(),
                        )


                        collectionRef.add(subItemModel).await()


                        withContext(Dispatchers.Main) {
                            Tools.showToast(context, "Saved")
                        }
                        updateData()
                        bottomSheet.dismiss()


                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Tools.showToast(context, e.message.toString())
                        }
                    }

                }
            }
        }


        btnCancel?.setOnClickListener {
            bottomSheet.dismiss()
        }

        imgCover?.setOnClickListener {

            pickupImage(requireContext())
        }
        bottomSheet.show()

    }

    private fun updateData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val getMainDocRef = args.mainItemDocRef
                Log.i("DetailFragment", getMainDocRef.toString())

                val getUserSubItem: MutableList<SubItem> =
                    fireStore.collection("subMainItem")
                        .whereEqualTo("main_item_id", getMainDocRef).get().await()
                        .toObjects(SubItem::class.java)

                withContext(Dispatchers.Main) {
                    Tools.showToast(requireContext(), getUserSubItem.toString())
                }

                withContext(Dispatchers.Main) {
                    setupRecyclerView(getUserSubItem)
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {
                    Tools.showToast(requireContext(), e.message.toString())
                }

                withContext(Dispatchers.Main) {
                    emptyList<SubItem>()
                }

            }
        }
    }

    private fun setupRecyclerView(userSubItem: MutableList<SubItem>) {
        detailAdapter = DetailAdapter(userSubItem, this)
        binding.detailRecyclerview.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            this.adapter = detailAdapter
        }
    }

    override fun onItemClick(position: Int) {

        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.dialog_create_add_sub_item)
        val btnCancel = bottomSheetDialog.findViewById<TextView>(R.id.btn_dialog_sub_cancel)
        val btnSave = bottomSheetDialog.findViewById<TextView>(R.id.btn_dialog_sub_save)
        val imgCover = bottomSheetDialog.findViewById<ImageView>(R.id.img_dialog_sub_thumbnail)
        val imgIncrease = bottomSheetDialog.findViewById<ImageView>(R.id.img_dialog_increase)
        val imgDecrease = bottomSheetDialog.findViewById<ImageView>(R.id.img_item_decrease)
        val edtInputNum = bottomSheetDialog.findViewById<EditText>(R.id.edt_item_number)
        val edtInputName = bottomSheetDialog.findViewById<EditText>(R.id.edt_dialog_sub_input)
        val currentTime = System.currentTimeMillis()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val getMainDocRef = args.mainItemDocRef
                val subItemCollectionRef =
                    fireStore.collection("subMainItem").whereEqualTo("main_item_id", getMainDocRef)
                        .get().await()
                val subItemDocRef = subItemCollectionRef.documents[position]

                if (subItemDocRef != null) {
                    val subItemModel = subItemDocRef.toObject(SubItem::class.java)
                    withContext(Dispatchers.Main) {
                        edtInputName?.hint = subItemModel?.name
                        edtInputNum?.hint = subItemModel?.item_count.toString()
                        Glide.with(requireContext()).load(subItemModel?.img_thumbnail).into(imgCover!!)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Tools.showToast(requireContext(), e.message.toString())
                }
            }

        }

        btnSave?.setOnClickListener {

            if (edtInputName?.text?.isNotEmpty() == true) {

                CoroutineScope(Dispatchers.IO).launch {
                    val getArgsMainDocID = args.mainItemDocRef
                    val subItemCollectionRef =
                        fireStore.collection("subMainItem")
                            .whereEqualTo("main_item_id", getArgsMainDocID).get().await()
                    val subItemDocRef = subItemCollectionRef.documents[position]
                    val docID = subItemDocRef.id

                    try {

                        val subItemMap = mutableMapOf<String, Any>()
                        subItemMap["name"] = edtInputName.text.toString()
                        subItemMap["update_date"] = currentTime.toString()
                        subItemMap["item_count"] = edtInputNum?.text.toString().toInt()
                        subItemMap["img_thumbnail"] = getImageURL.toString()

                        fireStore.collection("subMainItem").document(docID).update(subItemMap)
                            .await()

                        withContext(Dispatchers.Main) {
                            Tools.showToast(requireContext(), "Updated")
                        }

                        updateData()
                        bottomSheetDialog.dismiss()


                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Tools.showToast(requireContext(), e.message.toString())
                        }
                    }

                }
            }

        }
        btnCancel?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        imgCover?.setOnClickListener {
            pickupImage(requireContext())
        }

        bottomSheetDialog.show()
    }

    private fun pickupImage(context: Context) {
        if (Permissions.hasReadExternalStoragePermission(context)) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, Constants.IMAGE_REQUEST_CODE)
        } else {
            Permissions.requestReadExternalStoragePermission(this)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE) {
            imageUri = data?.data!!

            saveToStorage(imageUri)


        }
    }

    private fun saveToStorage(imageUri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val randomName = UUID.randomUUID().toString()
                val imgStorageRef = storage.getReference("itemImages/$randomName.jpg")
                val saveStorage = imgStorageRef.putFile(imageUri).await()
                getImageURL = saveStorage.storage.downloadUrl.await()

                withContext(Dispatchers.Main) {

                    Tools.showToast(requireContext(), "Upload Image is completed")
                }

                withContext(Dispatchers.Main) {
                    Tools.showToast(requireContext(), getImageURL.toString())
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {
                    Tools.showToast(requireContext(), e.message.toString())
                }
            }

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}