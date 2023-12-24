package com.example.inventory.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat

// data class entity
// 建立 item Entity
@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true) // 將 id 設為 primary key
    val id: Int = 0, // 將 id 的預設值設為 0
    @ColumnInfo(name = "name") // 將 itemName 設為 column，name 設為 column name
    val itemName: String,
    @ColumnInfo(name = "price") // 將 itemPrice 設為 column，price 設為 column name
    val itemPrice: Double,
    @ColumnInfo(name = "quantity") // 將 quantityInStock 設為 column，quantity 設為 column name
    val quantityInStock: Int
)

// 新增 Item 的 extension function
fun Item.getFormatPrice():String = NumberFormat.getCurrencyInstance().format(itemPrice)
