package com.ewadus.marketlist.data

data class SubItem(
    val name: String,
    val create_date: String,
    val update_date: String,
    val item_count: Int,
    val main_item_id: String? = null
) {
}