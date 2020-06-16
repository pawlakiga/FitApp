package com.example.fitappka.newtraining

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BreakDialogViewModel: ViewModel(){
    private val _counter = MutableLiveData<Int>()
    val counter: LiveData<Int>
        get() = _counter

    private val _settingDoneFlag = MutableLiveData<Boolean>()
    val settingDoneFlag: LiveData<Boolean>
        get() = _settingDoneFlag

    init {
        _counter.value = 0
        _settingDoneFlag.value = false
    }

    fun increment(){
        _counter.value = _counter.value?.plus(1)
    }

    fun decrement(){
        val sth = _counter.value?.compareTo(0) ?: 0
        if(sth > 0) {
            _counter.value = _counter.value?.minus(1)
        }
    }

    fun setFlag(){
        _settingDoneFlag.value = true
    }

    fun unSetFlag(){
        _settingDoneFlag.value = false
    }
}