package com.tsquaredapplications.waterquality.presenters


import android.os.Bundle

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
import kotlinx.android.synthetic.main.reading_card_view.view.*

class ReadingsFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(this).get(MainActivityViewModel::class.java) }
    lateinit var tempCard: View
    lateinit var flowCard: View
    lateinit var tdsCard: View
    lateinit var phCard: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_readings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tempCard = temp_view
        flowCard = flow_view
        tdsCard = tds_view
        phCard = ph_view

        flowCard.ticker.setCharacterLists(TickerUtils.provideNumberList())
        tempCard.ticker.setCharacterLists(TickerUtils.provideNumberList())
        tdsCard.ticker.setCharacterLists(TickerUtils.provideNumberList())
        phCard.ticker.setCharacterLists(TickerUtils.provideNumberList())

        flowCard.spark_view.sparkAnimator = MorphSparkAnimator()
        tempCard.spark_view.sparkAnimator = MorphSparkAnimator()
        tdsCard.spark_view.sparkAnimator = MorphSparkAnimator()
        phCard.spark_view.sparkAnimator = MorphSparkAnimator()

        flowCard.label.text = getString(R.string.flow)
        tempCard.label.text = getString(R.string.temp)
        tdsCard.label.text = getString(R.string.tds)
        phCard.label.text = getString(R.string.ph)

        tempCard.unit_label.text = getString(R.string.temp_unit)
        flowCard.unit_label.text = getString(R.string.flow_unit)
        tdsCard.unit_label.text = getString(R.string.tds_unit)

//        val params = readings_root_layout.layoutParams as FrameLayout.LayoutParams
//        val tv = TypedValue()
//        var actionBarHeight = 0
//        if (activity!!.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
//            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
//        }
//        params.bottomMargin = 8 + actionBarHeight
//        readings_root_layout.layoutParams = params

    }

    override fun onResume() {
        super.onResume()
        // observe data flow
        viewModel.getDataStream().observe(this, WaterDataObserver())
    }

    inner class WaterDataObserver : Observer<List<WaterData>> {
        private val flowList = arrayListOf<Float>()
        private val tempList = arrayListOf<Float>()
        private val tdsList = arrayListOf<Float>()
        private val phList = arrayListOf<Float>()

        var flowAdapter: SparkViewFloatAdapter? = null
        var tempAdapter: SparkViewFloatAdapter? = null
        var tdsAdapter: SparkViewFloatAdapter? = null
        var phAdapter: SparkViewFloatAdapter? = null


        override fun onChanged(it: List<WaterData>?) {
            it?.let {
                for (reading in it) {
                    if (flowAdapter == null)
                        flowList.add(reading.flow)

                    if (tempAdapter == null)
                        tempList.add(reading.temp)

                    if (tdsAdapter == null)
                        tdsList.add(reading.tds)

                    if (phAdapter == null)
                        phList.add(reading.pH)

                }
                if (it.size > 1) {
                    // FLOW
                    flowCard.ticker.text = (it[it.size - 1].flow.toString())

                    if (flowAdapter == null) {
                        flowAdapter = SparkViewFloatAdapter(flowList)
                        flowCard.spark_view.adapter = flowAdapter
                    } else
                        flowAdapter!!.addReading(it[it.size - 1].flow)

                    // TEMP
                    tempCard.ticker.text = (it[it.size - 1].temp.toString())

                    if (tempAdapter == null) {
                        tempAdapter = SparkViewFloatAdapter(tempList)
                        tempCard.spark_view.adapter = tempAdapter
                    } else
                        tempAdapter!!.addReading(it[it.size - 1].temp)

                    // TDS
                    tdsCard.ticker.text = (it[it.size - 1].tds.toString())

                    if (tdsAdapter == null) {
                        tdsAdapter = SparkViewFloatAdapter(tdsList)
                        tdsCard.spark_view.adapter = tdsAdapter
                    } else
                        tdsAdapter!!.addReading(it[it.size - 1].tds)

                    // pH
                    phCard.ticker.text = (it[it.size - 1].pH.toString())

                    if (phAdapter == null) {
                        phAdapter = SparkViewFloatAdapter(phList)
                        phCard.spark_view.adapter = phAdapter
                    } else
                        phAdapter!!.addReading(it[it.size - 1].pH)
                }

                else {
                    // TODO DISPLAY EMPTY VIEW
                }
            }
        }
    }
}
