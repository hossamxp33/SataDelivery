package com.sata.satadelivery.repository

import com.sata.satadelivery.di.IoDispatcher
import com.sata.satadelivery.models.auth.Driver
import com.sata.satadelivery.models.current_orders.DateModel
import com.sata.satadelivery.models.current_orders.OrderStatus
import com.sata.satadelivery.models.current_orders.OrdersItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import java.io.IOException
import javax.inject.Inject

class DataRepo @Inject constructor(
    private val Datasources: DataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
)  {
  
    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }
    val io = ioDispatcher+coroutineExceptionHandler
        val getOrders: Flow<Result<ArrayList<OrdersItem>>> =
        flow {
            emit(Datasources.getCurrentOrders())
        }
            .map { Result.success(it) }
            .retry(retries = 4) { t -> (t is IOException).also { if (it) {
                delay(1000 )
            }}}
            .catch {
                    throwable ->  emit(Result.failure(throwable)) }
            .flowOn(io)


        fun getDeliveryOrdersByDate(dateModel: DateModel?): Flow<Result<ArrayList<OrdersItem>>> =
        flow {
            emit(Datasources.getDeliveryOrdersByDate(dateModel))
        }
            .map { Result.success(it) }
            .retry(retries = 4) { t -> (t is IOException).also { if (it) {
                delay(1000 )
            }}}
            .catch {
                    throwable ->  emit(Result.failure(throwable)) }
            .flowOn(io)




    //changeOrderStatus

    suspend  fun changeOrderStatus(order_id:Int,data: OrderStatus): Flow<Result<OrderStatus>> =
        flow {
            emit(Datasources.changeOrderStatus(order_id,data))
        }
            .map {
                Result.success(it)
            }
            .retry(retries = 4) { t -> (t is IOException).also { if (it) {
                delay(1000)
            }}}
            .catch {

                    throwable ->  emit(Result.failure(throwable)) }

            .flowOn(io)



    //changeDeliveryStatus
    suspend  fun changeDeliveryStatus(Id:Int,statusId:Int): Flow<Result<OrdersItem>> =
        flow {
            emit(Datasources.changeDeliveryStatus(Id,statusId))
        }
            .map {
                Result.success(it)
            }
            .retry(retries = 4) { t -> (t is IOException).also { if (it) {
                delay(1000)
            }}}
            .catch {

                    throwable ->  emit(Result.failure(throwable)) }

            .flowOn(io)

//deliversOrdersCanceled
suspend  fun deliversOrdersCanceled(data:OrdersItem): Flow<Result<OrdersItem>> =
    flow {
        emit(Datasources.deliversOrdersCanceled(data))
    }
        .map {
            Result.success(it)
        }
        .retry(retries = 4) { t -> (t is IOException).also { if (it) {
            delay(1000)
        }}}
        .catch {

                throwable ->  emit(Result.failure(throwable)) }

        .flowOn(io)
    //editDeliveryData
    suspend  fun editDeliveryData( file: MultipartBody.Part?, name : String?, phone:String? ,id: Int?,): Flow<Result<Driver>> =
        flow {
            emit(Datasources.editDeliveryData(file,name,phone,id!!))
        }
            .map {
                Result.success(it)
            }
            .retry(retries = 4) { t -> (t is IOException).also { if (it) {
                delay(1000)
            }}}
            .catch {

                    throwable ->  emit(Result.failure(throwable)) }
            .flowOn(io)




}