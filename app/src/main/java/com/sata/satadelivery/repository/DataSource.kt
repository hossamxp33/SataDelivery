package com.sata.satadelivery.repository

import com.sata.satadelivery.models.auth.AuthModel
import com.sata.satadelivery.models.auth.Driver
import com.sata.satadelivery.models.auth.User
import com.sata.satadelivery.models.current_orders.DateModel
import com.sata.satadelivery.models.current_orders.OrderStatus
import com.sata.satadelivery.models.current_orders.OrdersItem
import com.sata.satadelivery.models.delivery.Delivery
import okhttp3.MultipartBody

import retrofit2.Response


/**
Created by Prokash Sarkar on Tue, January 19, 2021
 **/


interface DataSource {

    suspend fun getLoginResponse(loginModel: User): Response<AuthModel>

    suspend fun getCurrentOrders(): ArrayList<OrdersItem>

    suspend fun registerToken(loginModel: AuthModel): Response<AuthModel>
    suspend fun sendNotificationToDevice(loginModel: AuthModel): Response<AuthModel>

    suspend fun getDeliveryOrdersByDate(dateModel: DateModel?): ArrayList<OrdersItem>
    suspend fun changeOrderStatus(order_id:Int,data: OrderStatus) : OrderStatus

    suspend fun editDeliveryData(img: MultipartBody.Part?, name : String?, phone:String?,id :Int?) : Driver

    suspend fun getDeliversStatus(id :Int?) : Response<Delivery>

    suspend fun changeDeliveryStatus(id :Int?,statusId:Int) : OrdersItem
    suspend fun deliversOrdersCanceled(data:OrdersItem) : OrdersItem



}