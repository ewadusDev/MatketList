package com.ewadus.marketlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ewadus.marketlist.R
import com.ewadus.marketlist.data.SubItem
import kotlinx.android.synthetic.main.itemview_sub_item.view.*

class DetailAdapter(private val subItem: MutableList<SubItem>,
                    private val listener: OnItemClickListener ):RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {


    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }


    inner class DetailViewHolder(itemView:View):RecyclerView.ViewHolder(itemView),View.OnClickListener{
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.itemview_sub_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {

        holder.itemView.apply {
            tv_item_date_time.text = subItem[position].update_date
            tv_item_sub_title.text = subItem[position].name
            edt_dialog_number.text = subItem[position].item_count.toString()
            Glide.with(holder.itemView).load(subItem[position].img_thumbnail).into(img_item_sub_thumbnail)

        }



    }

    override fun getItemCount(): Int {
        return subItem.size
    }

}