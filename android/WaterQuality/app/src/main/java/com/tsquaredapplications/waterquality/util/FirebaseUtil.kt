package com.tsquaredapplications.waterquality.util

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseUtil {
    companion object {
        fun getDataRef(): DatabaseReference{
            return FirebaseDatabase.getInstance().reference
                .child("DATA")
        }
    }
}