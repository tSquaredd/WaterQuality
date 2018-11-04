package com.tsquaredapplications.waterquality.presenters


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.R.attr.theme
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.robinhood.spark.SparkView
import com.robinhood.spark.animation.MorphSparkAnimator
import com.robinhood.ticker.TickerUtils


import com.tsquaredapplications.waterquality.R
import com.tsquaredapplications.waterquality.adapters.SparkViewFloatAdapter
import com.tsquaredapplications.waterquality.data.WaterData
import com.tsquaredapplications.waterquality.util.FloatUtil
import com.tsquaredapplications.waterquality.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_history_graph.*
import java.util.*
import java.util.Arrays.asList


class HistoryGraphFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(this).get(MainActivityViewModel::class.java) }
    var adapter: SparkViewFloatAdapter? = null
    var currentType = 0
    var dataList: List<WaterData> = listOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ticker setup
        max_ticker.setCharacterLists(TickerUtils.provideNumberList())
        min_ticker.setCharacterLists(TickerUtils.provideNumberList())
        avg_ticker.setCharacterLists(TickerUtils.provideNumberList())
        status_ticker.setCharacterLists(TickerUtils.provideAlphabeticalList())

        // Spinner setup
        val dataset: List<String> = LinkedList(Arrays.asList("Flow", "Temp", "TDS", "pH"))
        graph_spinner.attachDataSource(dataset)

        graph_ticker.setCharacterLists(TickerUtils.provideNumberList())
        graph_spark.sparkAnimator = MorphSparkAnimator()

        graph_ticker_label.text = getString(R.string.flow_unit)

        viewModel.getDataStream().observe(this, Observer {
            dataList = it
            setupGraph(currentType, it)
        })

        graph_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentType = position
                setupGraph(currentType, dataList)
                setScrubber()
            }

        })
    }


    /**
     * TYPE
     *
     * 0 - flow
     * 1 - temp
     * 2 - tds
     * 3 - ph
     */
    fun setupGraph(type: Int, dataList: List<WaterData>) {
        var max = 0.0f
        var min = when(type){
            0 -> 50.0f
            1 -> 200.0f
            2 -> 2000.0f
            3 -> 20.0f
            else -> 3000.0f
        }

        var total = 0.0f

        val floatList = arrayListOf<Float>()
        when (type) {
            0 -> {
                // FLOW
                graph_ticker_label.text = getString(R.string.flow_unit)


                for (value in dataList) {
                    floatList.add(value.flow)
                    if (value.flow > max)
                        max = value.flow

                    if(value.flow < min)
                        min = value.flow

                    total += value.flow

                }

                val currentFlow = floatList[floatList.size - 1]

                graph_ticker.text = FloatUtil.round(currentFlow, 2).toString()
                adapter = SparkViewFloatAdapter(floatList)
                graph_spark.adapter = adapter

                when{
                    currentFlow == 0.0f -> {
                        status_ticker.text = getString(R.string.not_flowing)
                        status_ticker.setTextColor(resources.getColor(R.color.green)).toString()
                    }
                    else -> {
                        status_ticker.text = getString(R.string.flowing)
                        status_ticker.setTextColor(resources.getColor(R.color.black)).toString()
                    }
                }



            }
            1 -> {
                // TEMP
                graph_ticker_label.text = getString(R.string.temp_unit)

                for (value in dataList) {
                    floatList.add(value.temp)
                    if (value.temp > max)
                        max = value.temp

                    if(value.temp < min)
                        min = value.temp

                    total += value.temp
                }

                val currentTemp = floatList[floatList.size - 1]

                graph_ticker.text = FloatUtil.round(currentTemp, 2).toString()
                adapter = SparkViewFloatAdapter(floatList)
                graph_spark.adapter = adapter

                when{
                    currentTemp < 5.0f -> {
                        status_ticker.text = getString(R.string.freezing)
                        status_ticker.setTextColor(resources.getColor(R.color.red)).toString()
                    }
                    currentTemp > 80.0f -> {
                        status_ticker.text = getString(R.string.scalding)
                        status_ticker.setTextColor(resources.getColor(R.color.red)).toString()
                    }
                    else -> {
                        status_ticker.text = getString(R.string.great)
                        status_ticker.setTextColor(resources.getColor(R.color.green)).toString()
                    }
                }

            }
            2 -> {
                // TDS
                graph_ticker_label.text = getString(R.string.tds_unit)

                for (value in dataList) {
                    floatList.add(value.tds)

                    if (value.tds > max)
                        max = value.tds

                    if(value.tds < min)
                        min = value.tds

                    total += value .tds
                }

                val currentTds = floatList[floatList.size - 1]
                graph_ticker.text = FloatUtil.round(currentTds, 2).toString()

                adapter = SparkViewFloatAdapter(floatList)
                graph_spark.adapter = adapter

                when{
                    currentTds < 20 -> {
                        status_ticker.text = getString(R.string.good)
                        status_ticker.setTextColor(resources.getColor(R.color.green)).toString()
                    }
                    currentTds < 300.0f && currentTds >= 20 -> {
                        status_ticker.text = getString(R.string.excellent)
                        status_ticker.setTextColor(resources.getColor(R.color.green)).toString()

                    }
                    currentTds > 300.0f && currentTds < 500.0f -> {
                        status_ticker.text = getString(R.string.good)
                        status_ticker.setTextColor(resources.getColor(R.color.green)).toString()
                    }
                    currentTds > 500.0f && currentTds < 700.0f -> {
                        status_ticker.text = getString(R.string.fair)
                        status_ticker.setTextColor(resources.getColor(R.color.yellow)).toString()

                    }
                    else -> {
                        status_ticker.text = getString(R.string.poor)
                        status_ticker.setTextColor(resources.getColor(R.color.red)).toString()
                    }
                }

            }
            3 -> {
                // pH
                graph_ticker_label.text = ""

                for (value in dataList) {
                    floatList.add(value.pH)

                    if (value.pH > max)
                        max = value.pH

                    if(value.pH < min)
                        min = value.pH

                    total += value.pH
                }

                val currentPh = floatList[floatList.size - 1]
                graph_ticker.text = FloatUtil.round(currentPh, 2).toString()

                adapter = SparkViewFloatAdapter(floatList)
                graph_spark.adapter = adapter

                when{
                    currentPh < 6.5 -> {
                        status_ticker.text = getString(R.string.danger)
                        status_ticker.setTextColor(resources.getColor(R.color.red)).toString()
                    }
                    currentPh >= 6.5 && currentPh < 7 -> {
                        status_ticker.text =getString(R.string.good)
                        status_ticker.setTextColor(resources.getColor(R.color.green)).toString()
                    }
                    currentPh >= 7 && currentPh < 7.5 -> {
                        status_ticker.text =getString(R.string.excellent)
                        status_ticker.setTextColor(resources.getColor(R.color.green)).toString()

                    }
                    currentPh >= 7.5 && currentPh < 9 -> {
                        status_ticker.text =getString(R.string.good)
                        status_ticker.setTextColor(resources.getColor(R.color.green)).toString()
                    }
                    else -> {
                        status_ticker.text = getString(R.string.danger)
                        status_ticker.setTextColor(resources.getColor(R.color.red)).toString()
                    }
                }

            }
        }
        // Set stat card values
        max_ticker.text = FloatUtil.round(max, 2).toString()
        min_ticker.text = FloatUtil.round(min, 2).toString()

        val avg = total / dataList.size
        avg_ticker.text = FloatUtil.round(avg,2).toString()
    }

    fun setScrubber() {
        graph_spark.setScrubListener {
            if (it == null){
                val scrubValue = when(currentType){
                    0 -> dataList[dataList.size - 1].flow
                    1 -> dataList[dataList.size - 1].temp
                    2 -> dataList[dataList.size - 1].tds
                    3 -> dataList[dataList.size - 1].pH
                    else -> 0.0f
                }

                graph_ticker.text = FloatUtil.round(scrubValue, 2).toString()
                val date = dataList[dataList.size - 1]
                val dateString = "${date.month}/${date.day}/${date.year} ${date.hour}:${date.min}.${date.sec}"
                graph_date_display.text = dateString

            }

            else {
                graph_ticker.text = FloatUtil.round((it as Pair<Float, Int>).first,2).toString()
                val index = (it as Pair<Float, Int>).second
                val date = dataList[index]
                val dateString = "${date.month}/${date.day}/${date.year} ${date.hour}:${date.min}.${date.sec}"
                graph_date_display.text = dateString

            }
        }
    }
}
