package com.example.testltech

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.element_item.view.*

class MyAdapter (var list:ArrayList<Element>, listener: MyAdapterListener):
RecyclerView.Adapter<MyAdapter.MyViewHolder>()  {

    var listener: MyAdapterListener

    init {
        this.listener = listener
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {

        val v = LayoutInflater.from(p0.context).inflate(R.layout.element_item, p0, false)

        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        p0.bind(list[p1])
        applyClickEvents(p0, p1)

    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int) {

        holder.messageContainer.setOnClickListener(View.OnClickListener { listener.onMessageRowClicked(position) })
    }

    interface MyAdapterListener {
        fun onMessageRowClicked(position: Int)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTitle = itemView.tvTitle
        val tvText = itemView.tvText
        val tvDate = itemView.tvDate
        val ivImage = itemView.ivImage
        val messageContainer = itemView.rvItem

        fun bind(element: Element) {

            tvTitle.text = element.title
            tvText.text = element.text
            tvDate.text = element.date
            Picasso.get().load("http://dev-exam.l-tech.ru"+element.image).into(ivImage)

        }

    }
}

