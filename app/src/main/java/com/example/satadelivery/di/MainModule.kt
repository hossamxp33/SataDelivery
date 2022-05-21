package com.example.satadelivery.di

import androidx.lifecycle.ViewModel
import com.example.satadelivery.helper.ViewModelKey
import com.example.satadelivery.presentation.current_order_fragment.mvi.CurrentOrderViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
Created by Prokash Sarkar on Tue, January 19, 2021
 **/

@Module
interface MainModule {

    @Binds
    @IntoMap
    @ViewModelKey(CurrentOrderViewModel::class)
    fun currentOrderViewModel(mainViewModel: CurrentOrderViewModel): ViewModel


}