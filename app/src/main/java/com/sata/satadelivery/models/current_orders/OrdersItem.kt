package com.sata.satadelivery.models.current_orders

data class OrdersItem(
    var billing_address: BillingAddress? = null,
    var billing_address_id: Int? = null,
    var phone: String? = null,
    var branch_id: Int? = null,
    var commission: Double? = null,
    var coupon_id: Int? = null,
    var created: String? = null,
    var delivery_id: Int? = null,
    var delivery_serivce: Double? = null,
    var discount: Int? = null,
    var id: Int? = null,
    var modified: String? = null,
    var name: String? = null,
    var notes: String? = null,
    var offer_id: Int? = null,
    var order_details: List<OrderDetail>? = null,
    var order_status_id: Int? = null,
    var order_id: Int? = null,
    var paymenttype: Paymenttype? = null,
    var paymenttype_id: Int? = null,
    var taxes: Double? = null,
    var total: Double? = null,
    var offer_discount: Double?=null,
    var wallet_discount: Double?=null
)