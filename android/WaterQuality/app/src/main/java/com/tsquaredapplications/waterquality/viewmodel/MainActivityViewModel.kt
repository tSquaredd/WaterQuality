package com.tsquaredapplications.waterquality.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsquaredapplications.waterquality.data.DataRepository
import com.tsquaredapplications.waterquality.data.WaterData

class MainActivityViewModel: ViewModel() {
    private var repo: DataRepository

    init {
        repo = DataRepository()
    }

    fun getDataStream(): MutableLiveData<List<WaterData>>{
        return repo.getDataStream()
    }
}