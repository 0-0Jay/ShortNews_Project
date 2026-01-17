package com.example.shortnews.ui.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarViewModel : ViewModel() {

    val selectedDate = MutableLiveData<String>()

}