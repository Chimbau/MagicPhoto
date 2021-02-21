package com.example.magicphoto

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem


class FilterListAdapter(var image : ImageView)
    : RecyclerView.Adapter<FilterListAdapter.ViewHolder>() {

    var filterList: List<ThumbnailItem> = listOf()


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val filterIcon = view.findViewById<ImageView>(R.id.filter_icon)
        val filterText = view.findViewById<TextView>(R.id.filter_text)
    }
    private var bitmap : Bitmap? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.filter_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: FilterListAdapter.ViewHolder, position: Int) {
        holder.filterIcon.setImageBitmap(filterList[position].image)
        holder.filterText.text = filterList[position].filterName

        holder.itemView.setOnClickListener{
            if(bitmap == null){
                bitmap = image.drawToBitmap()
            }
            val new_bitmap = bitmap?.copy(bitmap?.config, true)

            image.setImageBitmap(filterList[position].filter.processFilter(new_bitmap))
        }
    }

    fun setData(list : MutableList<ThumbnailItem>){
        this.filterList = list
        if(bitmap!=null){
            bitmap = image.drawToBitmap()
        }
        notifyDataSetChanged()
    }


}