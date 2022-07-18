package com.sata.satadelivery.presentation.history_order_fragment.mvi



import com.sata.satadelivery.helper.UserError
import com.sata.satadelivery.models.current_orders.OrdersItem
import com.sata.satadelivery.repository.DataRepo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


/**
 * this is the model function in MVI, it's responsibility is to convert intents into views states
 */
suspend fun mapIntentToViewState(
    intent: MainIntent,
    Datarepo: DataRepo,
    loadMainData: suspend () -> Flow<Result<ArrayList<OrdersItem>>> = { Datarepo.getDeliveryOrdersByDate(intent.dateModel) },
) = when (intent) {
    is MainIntent.Initialize -> proceedWithInitialize(loadMainData, intent)
    is MainIntent.ErrorDisplayed -> intent.viewState.copy(error = null)
    is MainIntent.FilterData -> filterDataByState(intent, intent.stateId!!)

}

private suspend fun proceedWithInitialize(
    loadCart: suspend () -> Flow<Result<ArrayList<OrdersItem>>>,
    intent: MainIntent,
): MainViewState {
    val response = loadCart()
    val data = response.first()
    return runCatching {
        intent.viewState!!.copy(data = (data.getOrThrow()),
            filterData = data.getOrThrow(),
            noOrderYet = false,
            error = null,
            progress = false)
    }
        .getOrElse {
            intent.viewState!!.copy(error = UserError.NetworkError(it))
        }

}
private fun filterDataByState(intent:MainIntent, order_status_id: Int): MainViewState {
    val filterDataArray = filterOrder(order_status_id, intent.viewState?.data!!)
    return intent.viewState!!.copy(filterData = filterDataArray as ArrayList<OrdersItem>)
}

fun filterOrder(order_status_id: Int, productArray: ArrayList<OrdersItem>?) =
    productArray!!.filter { data -> data.order_status_id == order_status_id }







