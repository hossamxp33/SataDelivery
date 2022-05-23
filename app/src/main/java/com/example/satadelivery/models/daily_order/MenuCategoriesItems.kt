package com.example.satadelivery.models.daily_order

data class MenuCategoriesItems(
    var created: String,
    var descriptions: String,
    var descriptions_en: String,
    var id: Int,
    var menu_categories_id: Int,
    var modified: String,
    var name: String,
    var name_en: String,
    var photo: String,
    var price: Int
)