package com.tsquaredapplications.waterquality.presenters


import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.tsquaredapplications.waterquality.R
import com.tsquaredapplications.waterquality.viewmodel.MainActivityViewModel

class ReadingsFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(this).get(MainActivityViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_readings, container, false)
    }

    override fun onResume() {
        super.onResume()
        // observe data flow
        viewModel.getDataStream().observe(this, Observer {
            for(reading in it)
                Log.i("ReadingsFragment", "onResume: ${reading.toString()}")
        })
    }

}
