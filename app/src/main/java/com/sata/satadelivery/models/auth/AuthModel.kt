package com.sata.satadelivery.models.auth

data class AuthModel(
    var token: String?=null,
    var user: User?=null,
    var user_id: Int?=null,
    )