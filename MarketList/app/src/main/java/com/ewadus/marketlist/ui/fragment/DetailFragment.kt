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
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.text.SimpleDateFormat
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
    private var itemCount = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        bottomSheetDialog = BottomSheetDialog(requireContext())


        updateData()

        binding.fabAddSubItem.setOnClickListener {
            createData()
        }
        return binding.root
    }

    private fun createData() {

        bottomSheetDialog.setContentView(R.layout.dialog_create_add_sub_item)

        val btnSave = bottomSheetDialog.findViewById<TextView>(R.id.btn_dialog_sub_save)
        val btnCancel = bottomSheetDialog.findViewById<TextView>(R.id.btn_dialog_sub_cancel)
        val imgCover = bottomSheetDialog.findViewById<ImageView>(R.id.img_dialog_sub_thumbnail)
        val edtItemText = bottomSheetDialog.findViewById<EditText>(R.id.edt_dialog_sub_input)
        val edtItemCount = bottomSheetDialog.findViewById<EditText>(R.id.edt_dialog_number)
        val imgIncrease = bottomSheetDialog.findViewById<ImageView>(R.id.img_dialog_increase)
        val imgDecrease = bottomSheetDialog.findViewById<ImageView>(R.id.img_dialog_decrease)
        val getTime = System.currentTimeMillis()
        val converterTime = SimpleDateFormat("d/MMM/yyyy HH:mm")
        val formattedTime = converterTime.format(getTime)


        val collectionRef = fireStore.collection("subMainItem")

        btnSave?.setOnClickListener {
            if (edtItemText?.text?.isNotEmpty() == true && edtItemCount?.text?.isNotEmpty() == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val subItemModel = SubItem(
                            edtItemText.text.toString(),
                            formattedTime.toString(),
                            formattedTime.toString(),
                            itemCount,
                            args.mainItemDocRef,
                            getImageURL.toString(),
                        )
                        collectionRef.add(subItemModel).await()
                        withContext(Dispatchers.Main) {
                            Tools.showToast(requireContext(), "Saved")
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

        imgIncrease?.setOnClickListener {
            ++itemCount
            edtItemCount?.setText("$itemCount")

        }

        imgDecrease?.setOnClickListener {
            --itemCount
            edtItemCount?.setText("$itemCount")
        }


        btnCancel?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        imgCover?.setOnClickListener {
            pickupImage(requireContext())
        }
        bottomSheetDialog.show()

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
        detailAdapter =
            DetailAdapter(userSubItem, this)
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
        val imgDecrease = bottomSheetDialog.findViewById<ImageView>(R.id.img_dialog_decrease)
        val edtInputNum = bottomSheetDialog.findViewById<EditText>(R.id.edt_dialog_number)
        val edtInputName = bottomSheetDialog.findViewById<EditText>(R.id.edt_dialog_sub_input)
        val getTime = System.currentTimeMillis()
        val converterTime = SimpleDateFormat("d/MMM/yyyy HH:mm")
        val formattedTime = converterTime.format(getTime)

        // get item from db
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
                        edtInputName?.setText("${subItemModel?.name.toString()}")
                        itemCount = subItemModel?.item_count!!
                        edtInputNum?.setText("${itemCount}")
                        Glide.with(requireContext()).load(subItemModel?.img_thumbnail)
                            .into(imgCover!!)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Tools.showToast(requireContext(), e.message.toString())
                }
            }

        }

        imgIncrease?.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val getArgsMainDocID = args.mainItemDocRef
                val subItemCollectionRef =
                    fireStore.collection("subMainItem")
                        .whereEqualTo("main_item_id", getArgsMainDocID).get().await()
                val subItemDocRef = subItemCollectionRef.documents[position]
                val docID = subItemDocRef.id
                val itemCountMap = mutableMapOf<String, Any>()
                itemCountMap["item_count"] = itemCount

                try {
                    if (itemCount != null) {
                        ++itemCount
                        withContext(Dispatchers.Main) {
                            edtInputNum?.setText(itemCount.toString())
                        }
                        fireStore.collection("subMainItem").document(docID).update(itemCountMap)
                            .await()
                    }
                    Log.i("DetailFragment", itemCount.toString())

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Tools.showToast(requireContext(), e.message.toString())
                    }
                }

            }

        }

        imgDecrease?.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val getArgsMainDocID = args.mainItemDocRef
                val subItemCollectionRef =
                    fireStore.collection("subMainItem")
                        .whereEqualTo("main_item_id", getArgsMainDocID).get().await()
                val subItemDocRef = subItemCollectionRef.documents[position]
                val docID = subItemDocRef.id
                val itemCountMap = mutableMapOf<String, Any>()
                itemCountMap["item_count"] = itemCount

                try {
                    if (itemCount != null) {
                        --itemCount
                        withContext(Dispatchers.Main) {
                            edtInputNum?.setText(itemCount.toString())
                        }
                        fireStore.collection("subMainItem").document(docID).update(itemCountMap)
                            .await()
                    }
                    Log.i("DetailFragment", itemCount.toString())

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Tools.showToast(requireContext(), e.message.toString())
                    }
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
                        subItemMap["update_date"] = formattedTime.toString()
                        subItemMap["item_count"] = itemCount!!
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
            updateData()
        }

        imgCover?.setOnClickListener {
            pickupImage(requireContext())
        }
        bottomSheetDialog.setCanceledOnTouchOutside(false)
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
            val bindCreateBottomSheet =
                bottomSheetDialog.findViewById<ImageView>(R.id.img_dialog_sub_thumbnail)
            Glide.with(requireContext()).load(imageUri).into(bindCreateBottomSheet!!)
            saveImgToStorage(imageUri)
        }
    }

    private fun saveImgToStorage(imageUri: Uri) {
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