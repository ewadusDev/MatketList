package com.ewadus.marketlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ewadus.marketlist.R
import com.ewadus.marketlist.data.MainItem
import kotlinx.android.synthetic.main.itemview_main_item.view.*


class HomeAdapter(
    private val itemList: MutableList<MainItem>,
    private val listener:OnItemClickListener,
    private var optionsMenuClickListener: OptionsMenuClickListener
) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    interface OptionsMenuClickListener {
        fun onOptionsMenuClicked(position: Int)
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }


   inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
       override fun onClick(v: View?) {
           val position:Int = adapterPosition
           if (position != RecyclerView.NO_POSITION){
               listener.onItemClick(position)
           }
       }

       init {
           itemView.setOnClickListener(this)
       }

   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemview_main_item, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.itemView.apply {
            tv_main_name.text = itemList[position].name
            tv_time_stamp.text = itemList[position].update_date
            item_option_menu.setOnClickListener {
                optionsMenuClickListener.onOptionsMenuClicked(position)

            }
        }
    }

}







