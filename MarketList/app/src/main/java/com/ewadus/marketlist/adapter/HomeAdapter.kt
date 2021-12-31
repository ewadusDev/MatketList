package com.ewadus.marketlist.adapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ewadus.marketlist.R
import com.ewadus.marketlist.data.MainItem
import kotlinx.android.synthetic.main.itemview_main_item.view.*


class HomeAdapter(
    private val itemList: MutableList<MainItem>,
    private var optionsMenuClickListener: OptionsMenuClickListener
) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {


    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemview_main_item, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.itemView.apply {
            tv_main_name.text = itemList[position].name
            tv_time_stamp.text = itemList[position].update_date
            item_option_menu.setOnClickListener {
                optionsMenuClickListener.onOptionsMenuClicked(position)

            }
            holder.itemView.setOnClickListener {
//                findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
                Toast.makeText(holder.itemView.context, "in developing", Toast.LENGTH_LONG).show()
            }


        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    interface OptionsMenuClickListener {
        fun onOptionsMenuClicked(position: Int)
    }


}


