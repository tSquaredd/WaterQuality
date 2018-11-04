package com.tsquaredapplications.waterquality.adapters

import com.robinhood.spark.SparkAdapter

class SparkViewFloatAdapter(val yData: ArrayList<Float>): SparkAdapter() {


    override fun getY(index: Int): Float {
        return yData[index]
    }

    override fun getItem(index: Int): Any {
       return yData[index]
    }

    override fun getCount(): Int {
        return yData.size
    }

    fun addReading(value: Float){
        yData.add(value)
        notifyDataSetChanged()
    }

    fun isEmpty() = yData.isEmpty()
}