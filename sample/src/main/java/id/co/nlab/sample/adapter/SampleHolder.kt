package id.co.nlab.sample.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_sample.view.*

class SampleHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(data: SampleModel){
        itemView.txt.text = data.name
    }
}