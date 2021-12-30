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
import java.lang.Exception

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var homeAdapter: HomeAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        updateDataToList()

        binding.swipeRefresh.setOnRefreshListener {
            pullToRefresh()
        }

        binding.fabAdd.setOnClickListener {
            updateSheetBottomUI(requireContext())
        }

        return binding.root


    }

    private fun updateDataToList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allMainItem: MutableList<MainItem> =
                    Firebase.firestore.collection("mainItems").get().await()
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
                    Firebase.firestore.collection("mainItems").get().await()
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
                       Toast.makeText(requireContext(),"Rename${position}",Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.btn_delete -> {
                        Toast.makeText(requireContext(),"Delete${item.itemId}",Toast.LENGTH_SHORT).show()

                        return true
                    }

                }
                return false
            }

        })
        popupMenu.show()
    }


    private fun updateSheetBottomUI(context: Context) {

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
                            currentTime.toString()
                        )
                        itemCollectionRef.add(dataMainItem).await()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Item was saved", Toast.LENGTH_LONG).show()
                        }

                        updateDataToList()
                        bottomSheet.dismiss()

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()

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



