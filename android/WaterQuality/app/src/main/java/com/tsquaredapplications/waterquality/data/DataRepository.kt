package com.tsquaredapplications.waterquality.data

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.tsquaredapplications.waterquality.util.FirebaseUtil

class DataRepository {
    private var databaseRef: DatabaseReference = FirebaseUtil.getDataRef()
    private var dataStream = MutableLiveData<List<WaterData>>()

    init {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                val data = mutableListOf<WaterData>()
                val children = p0.children
                for (child in children) {
                    val currentData = child.getValue(WaterData::class.java)
                    currentData?.let {
                        data.add(it)
                    }
                }

                dataStream.value = data

            }

        })
    }

    fun getDataStream(): MutableLiveData<List<WaterData>>{
        return dataStream
    }
}