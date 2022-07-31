package com.sata.satadelivery.presentation.current_order_fragment.mvi


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sata.satadelivery.helper.BaseViewModel
import com.sata.satadelivery.models.auth.AuthModel
import com.sata.satadelivery.models.current_orders.OrderStatus
import com.sata.satadelivery.models.current_orders.OrdersItem
import com.sata.satadelivery.models.delivery.Delivery
import com.sata.satadelivery.models.delivery.DeliveryItem
import com.sata.satadelivery.repository.DataRepo
import com.sata.satadelivery.repository.RemoteDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class CurrentOrderViewModel @Inject constructor(private val DateRepoCompnay: DataRepo, private val Datasources: RemoteDataSource,) :
    BaseViewModel<MainViewState>() {

    val intents : Channel<MainIntent> = Channel<MainIntent>(Channel.UNLIMITED)

    protected val uiState : MutableStateFlow<MainViewState?> = MutableStateFlow(MainViewState())

    val state: MutableStateFlow<MainViewState?> get() = uiState

    private var job: Job? = null

    var  mclientLatitude = MutableLiveData<Double>()
    var  mclientLongitude = MutableLiveData<Double>()

    protected val ChangeStatusuiState : MutableStateFlow<OrdersItem>? = null

    val OrderState: MutableStateFlow<OrdersItem>? get() = ChangeStatusuiState

    var deliveryItemLD: MutableLiveData<Delivery>? = null
    var OrderStateLD: MutableLiveData<OrderStatus>? = null

    protected val getStatusState : MutableStateFlow<DeliveryItem>? = null

    var authLD: MutableLiveData<AuthModel>? = null

    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }
    init {
         getIntent()
        mclientLatitude = MutableLiveData()
        mclientLatitude = MutableLiveData()
        deliveryItemLD = MutableLiveData()
        OrderStateLD= MutableLiveData()
        authLD= MutableLiveData()
    }
    fun getIntent() {

        job = viewModelScope.launch() {
            uiState.value = MainViewState().copy(progress = true)
            uiState.value = MainViewState().copy(noOrderYet = true)

            intents.receiveAsFlow().collect {
                uiState.value = (mapIntentToViewState(it, DateRepoCompnay))
            }
        }
    }

    fun getLatLong(latitude: Double?,longitude:Double?) {
         mclientLatitude.postValue(latitude!!)
        mclientLongitude.postValue(longitude!!)

    }
    fun changeOrderStatus(Id:Int,data: OrderStatus) {
        job = CoroutineScope(Dispatchers.IO+coroutineExceptionHandler).launch {
            val response = DateRepoCompnay.changeOrderStatus(Id,data)
            withContext(Dispatchers.Main+coroutineExceptionHandler) {
                (response.collect {
                    runCatching {
                        OrderStateLD?.value = it.getOrNull()!!

                    }.getOrElse {
                        onError("Error : ${it.message} ")

                    }
                })
            }
        }

    }
    //authentication
    fun registerToken(loginModel: AuthModel?) {
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }
        job = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            val response = Datasources.registerToken(loginModel!!)
            withContext(Dispatchers.Main + coroutineExceptionHandler) {
                if (response.isSuccessful) {
                    authLD?.postValue(response.body())

                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }

    }

    //authentication
    fun sendNotificationToDevice(loginModel: AuthModel?) {
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }
        job = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            val response = Datasources.sendNotificationToDevice(loginModel!!)
            withContext(Dispatchers.Main + coroutineExceptionHandler) {
                if (response.isSuccessful) {
                    authLD?.postValue(response.body())

                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }

    }

    fun deliversOrdersCanceled(data:OrdersItem) {
        job = CoroutineScope(Dispatchers.IO+coroutineExceptionHandler).launch {
            val response = DateRepoCompnay.deliversOrdersCanceled(data)
            withContext(Dispatchers.Main+coroutineExceptionHandler) {
                (response.collect {
                    runCatching {
                        OrderState?.value = it.getOrNull()!!

                    }.getOrElse {
                        onError("Error : ${it.message} ")

                    }
                })
            }
        }

    }
//changeDeliveryStatus
fun changeDeliversStatus(Id:Int,statusId:Int) {
    job = CoroutineScope(Dispatchers.IO+coroutineExceptionHandler).launch {
        val response = DateRepoCompnay.changeDeliveryStatus(Id, statusId)
        withContext(Dispatchers.Main+coroutineExceptionHandler) {
            (response.collect {
                runCatching {
                    OrderState?.value = it.getOrNull()!!

                }.getOrElse {
                    onError("Error : ${it.message} ")

                }
            })
        }
    }
}

    fun getDeliversStatus(Id:Int) {
        job = CoroutineScope(Dispatchers.IO+coroutineExceptionHandler).launch {
            val response = Datasources.getDeliversStatus(Id)
            withContext(Dispatchers.Main+coroutineExceptionHandler) {
                if (response.isSuccessful) {
                    deliveryItemLD?.postValue(response.body())

                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }

    }



    private fun onError(message: String) {
//        errorMessage.value = message
//        loading.value = false
    }
    override fun onCleared() {

        super.onCleared()
        job!!.cancel()

    }
}
