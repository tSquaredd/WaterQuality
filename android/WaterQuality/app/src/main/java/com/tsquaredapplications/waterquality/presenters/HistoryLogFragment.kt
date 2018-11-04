package com.tsquaredapplications.waterquality.presenters


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsquaredapplications.waterquality.MainActivity

import com.tsquaredapplications.waterquality.R
import com.tsquaredapplications.waterquality.adapters.ReadingLogAdapter
import com.tsquaredapplications.waterquality.data.WaterData
import com.tsquaredapplications.waterquality.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_history_log.*


class HistoryLogFragment : androidx.fragment.app.Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    var adapter: ReadingLogAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDataStream().observe(this, Observer {
            if (adapter == null){
                adapter = ReadingLogAdapter(ArrayList(it.reversed()), context!!)
                val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                recycler_log.layoutManager = layoutManager
                recycler_log.adapter = adapter

            }
            else {
                adapter?.newData(it[it.size - 1])
            }
        })


    }


}
