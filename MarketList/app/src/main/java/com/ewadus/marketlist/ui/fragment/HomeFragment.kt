package com.ewadus.marketlist.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ewadus.marketlist.R
import com.ewadus.marketlist.adapter.HomeAdapter
import com.ewadus.marketlist.data.MainItem
import com.ewadus.marketlist.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.Exception

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var uid : String
//    private val args by  navArgs<HomeFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        uid = auth.currentUser?.uid.toString()

        updateDataToList()

        binding.swipeRefresh.setOnRefreshListener {
            pullToRefresh()
        }

        binding.fabAdd.setOnClickListener {
            addDataToDatabase(requireContext())
        }

        return binding.root


    }

    private fun updateDataToList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allMainItem: MutableList<MainItem> =
                    Firebase.firestore.collection("mainItems").whereEqualTo("uid",uid).get().await()
                        .toObjects(MainItem::class.java)
                withContext(Dispatchers.Main) {
                    setupRecyclerView(allMainItem)

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    emptyList<MainItem>()
                }
            }

        }
    }

    private fun pullToRefresh() {

        binding.swipeRefresh.isRefreshing = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allMainItem: MutableList<MainItem> =
                    Firebase.firestore.collection("mainItems").whereEqualTo("uid",uid).get().await()
                        .toObjects(MainItem::class.java)
                withContext(Dispatchers.Main) {
                    setupRecyclerView(allMainItem)
                    binding.swipeRefresh.isRefreshing = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    emptyList<MainItem>()
                }
                binding.swipeRefresh.isRefreshing = false
            }

        }
    }

    private fun setupRecyclerView(mainItemList: MutableList<MainItem>) {
        homeAdapter = HomeAdapter(mainItemList, object : HomeAdapter.OptionsMenuClickListener {
            override fun onOptionsMenuClicked(position: Int) {
                performOptionsMenuClick(position)

            }

        })

        binding.homeRecyclerview.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            this.adapter = homeAdapter
        }
    }

    private fun performOptionsMenuClick(position: Int) {
        val popupMenu = PopupMenu(
            requireContext(),
            binding.homeRecyclerview[position].findViewById(R.id.item_option_menu)
        )
        popupMenu.inflate(R.menu.option_rename_del)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.btn_rename -> {
//                        Toast.makeText(requireContext(), "Rename${position}", Toast.LENGTH_SHORT)
//                            .show()
                        renameItem(position)
                        return true
                    }
                    R.id.btn_delete -> {
                        deleteData(position)
                        return true
                    }

                }
                return false
            }

        })
        popupMenu.show()
    }

    private fun renameItem(position: Int) {
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.dialog_create_add_main_item)

        val btnSave = bottomSheet.findViewById<TextView>(R.id.btn_save)
        val btnCancel = bottomSheet.findViewById<TextView>(R.id.btn_cancel)
        val edtAddItem = bottomSheet.findViewById<EditText>(R.id.edt_input_main_item)
        val updateTime = System.currentTimeMillis() / 1000

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val mainItemCollectionRef = fireStore.collection("mainItems").whereEqualTo("uid",uid).get().await()
                val documentRef = mainItemCollectionRef.documents[position]
                val mainItemDocumentID = documentRef.id
                val query = fireStore.collection("mainItems")
                    .document(mainItemDocumentID)
                    .get().await()
                if (query != null) {
                    val itemModel = query.toObject(MainItem::class.java)
                    withContext(Dispatchers.Main) {

                        edtAddItem?.hint = itemModel?.name

                        Toast.makeText(
                            requireContext(),
                            itemModel?.name,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: java.lang.Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT)
                        .show()

                }
            }

        }

        btnSave?.setOnClickListener {
            if (edtAddItem?.text?.isNotEmpty() == true) {
                CoroutineScope(Dispatchers.IO).launch {

                    val itemCollectionRef = fireStore.collection("mainItems").whereEqualTo("uid",uid).get().await()
                    val itemDocumentSnapshot = itemCollectionRef.documents[position]
                    val documentID = itemDocumentSnapshot.id

                    try {
                        val itemMainMap = mutableMapOf<String, Any>()
                        itemMainMap["name"] = edtAddItem.text.toString()
                        itemMainMap["update_date"] = updateTime.toString()
                        fireStore.collection("mainItems").document(documentID).update(itemMainMap)
                            .await()
                        updateDataToList()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "updated",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        bottomSheet.dismiss()


                    } catch (e: java.lang.Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                e.message.toString(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                }
            }

        }

        btnCancel?.setOnClickListener {
            bottomSheet.dismiss()
        }


        bottomSheet.show()

    }


    private fun deleteData(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                val getCollection = fireStore.collection("mainItems").whereEqualTo("uid",uid).get().await()
                val documentSnapshot = getCollection.documents[position]
                val documentID = documentSnapshot.id

                if (getCollection.documents.isNotEmpty()) {
                    fireStore.collection("mainItems").document(documentID).delete().await()
                    updateDataToList()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }
    }


    private fun addDataToDatabase(context: Context) {

        val bottomSheet = BottomSheetDialog(context)
        bottomSheet.setContentView(R.layout.dialog_create_add_main_item)

        val btnSave = bottomSheet.findViewById<TextView>(R.id.btn_save)
        val btnCancel = bottomSheet.findViewById<TextView>(R.id.btn_cancel)
        val edtAddItem = bottomSheet.findViewById<EditText>(R.id.edt_input_main_item)

        val itemCollectionRef = fireStore.collection("mainItems")
        val currentTime = System.currentTimeMillis() / 1000

        btnSave?.setOnClickListener {

            if (edtAddItem?.text?.isNotEmpty() == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val dataMainItem = MainItem(
                            edtAddItem.text.toString(),
                            currentTime.toString(),
                            currentTime.toString(),
                            uid
                        )
                        itemCollectionRef.add(dataMainItem).await()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Item was saved", Toast.LENGTH_LONG).show()
                        }
                        updateDataToList()
                        bottomSheet.dismiss()
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            } else {
                edtAddItem?.error = "Fill out"
            }
        }
        btnCancel?.setOnClickListener {
            bottomSheet.dismissWithAnimation
            bottomSheet.dismiss()
        }

        bottomSheet.show()


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}



