package com.tsquaredapplications.waterquality.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tsquaredapplications.waterquality.R
import com.tsquaredapplications.waterquality.data.WaterData
import kotlinx.android.synthetic.main.reading_log_item.view.*

class ReadingLogAdapter(val readingsList: ArrayList<WaterData>, val context: Context): RecyclerView.Adapter<ReadingLogAdapter.ReadingLogViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingLogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.reading_log_item, parent, false)
        return ReadingLogViewHolder(view)
    }

    override fun getItemCount(): Int {
        return readingsList.size
    }

    override fun onBindViewHolder(holder: ReadingLogViewHolder, position: Int) {
        val currentItem = readingsList[position]

        val dateString = "${currentItem.month}/${currentItem.day}/${currentItem.year}"
        holder.date.text = dateString

        val timeString = "${currentItem.hour}:${currentItem.min}.${currentItem.sec}"
        holder.time.text = timeString

        val flowString = context.getString(R.string.flow_log, currentItem.flow.toString())
        holder.flow.text = flowString

        val tempString = context.getString(R.string.temp_log, currentItem.temp.toString())
        holder.temp.text = tempString

        val tdsString = context.getString(R.string.tds_log, currentItem.tds.toString())
        holder.tds.text = tdsString

        val phString = context.getString(R.string.ph_log, currentItem.pH.toString())
        holder.ph.text = phString


    }

    inner class ReadingLogViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val date = itemView.date
        val time = itemView.time
        val temp = itemView.temp
        val flow = itemView.flow
        val tds = itemView.tds
        val ph = itemView.ph
    }

    fun newData(newItem: WaterData){
        readingsList.add(newItem)
        notifyItemChanged(readingsList.size - 1)
    }
}