package com.tsquaredapplications.waterquality.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tsquaredapplications.waterquality.R
import com.tsquaredapplications.waterquality.data.WaterData
import com.tsquaredapplications.waterquality.util.FloatUtil
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

        val min = if (currentItem.min < 10) "0${currentItem.min}" else "${currentItem.min}"
        val sec = if (currentItem.sec < 10) "0${currentItem.sec}" else "${currentItem.sec}"
        val timeString = "${currentItem.hour}:$min.$sec"
        holder.time.text = timeString

        val flowString = context.getString(R.string.flow_log, FloatUtil.round(currentItem.flow,2).toString())
        holder.flow.text = flowString

        val tempString = context.getString(R.string.temp_log, FloatUtil.round(currentItem.temp,2).toString())
        holder.temp.text = tempString

        val tdsString = context.getString(R.string.tds_log, FloatUtil.round(currentItem.tds,2).toString())
        holder.tds.text = tdsString

        val phString = context.getString(R.string.ph_log, FloatUtil.round(currentItem.pH,2).toString())
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
        notifyItemChanged(0)
    }
}