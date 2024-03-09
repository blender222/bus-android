package com.ashtar.bus.ui.home

import androidx.lifecycle.ViewModel
import com.ashtar.bus.data.BusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val busRepository: BusRepository
) : ViewModel() {
}