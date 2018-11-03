package com.tsquaredapplications.waterquality.presenters


import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.robinhood.spark.animation.MorphSparkAnimator
import com.robinhood.ticker.TickerUtils

import com.tsquaredapplications.waterquality.R
import com.tsquaredapplications.waterquality.adapters.SparkViewFloatAdapter
import com.tsquaredapplications.waterquality.data.WaterData
import com.tsquaredapplications.waterquality.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_readings.*

class ReadingsFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(this).get(MainActivityViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_readings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flow_ticker.setCharacterLists(TickerUtils.provideNumberList())
        temp_ticker.setCharacterLists(TickerUtils.provideNumberList())

        flow_spark_view.sparkAnimator = MorphSparkAnimator()
        temp_spark_view.sparkAnimator = MorphSparkAnimator()
    }

    override fun onResume() {
        super.onResume()
        // observe data flow
        viewModel.getDataStream().observe(this, WaterDataObserver())
    }

    inner class WaterDataObserver : Observer<List<WaterData>> {
        private val flowList = arrayListOf<Float>()
        private val tempList = arrayListOf<Float>()

        var flowAdapter: SparkViewFloatAdapter? = null

        override fun onChanged(it: List<WaterData>?) {
            it?.let {
                for (reading in it) {
                   if (flowAdapter == null)
                        flowList.add(reading.flow)

                    if (temp_spark_view.adapter == null)
                        tempList.add(reading.temp)
                }

                // FLOW
                flow_ticker.text = (it[it.size - 1].flow.toString())

                if (flowAdapter == null){
                    flowAdapter = SparkViewFloatAdapter(flowList)
                    flow_spark_view.adapter = flowAdapter
                }
                else
                    flowAdapter!!.addReading(it[it.size - 1].flow)

                // TEMP
                temp_ticker.text = (it[it.size - 1].temp.toString())
                if (temp_spark_view.adapter == null)
                    temp_spark_view.adapter = SparkViewFloatAdapter(tempList)
                else
                    (temp_spark_view.adapter as SparkViewFloatAdapter).addReading(it[it.size - 1].temp)

            }
        }

    }
}
