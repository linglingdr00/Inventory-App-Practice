package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// data access object (DAO)
// 建立 item DAO
@Dao
interface ItemDao {
    // insert suspend function
    @Insert(onConflict = OnConflictStrategy.IGNORE) // 發生衝突時會忽略 new item
    suspend fun insert(item: Item)

    // update suspend function
    @Update
    suspend fun update(item: Item)

    // delete suspend function
    @Delete
    suspend fun delete(item: Item)

    // 選取特定 item 的 所有 column
    @Query("SELECT * from item WHERE id = :id") // :id 為 getItem 中的引數(id)
    fun getItem(id: Int): Flow<Item>

    // 選取 item table 中的所有 columns，以 name 遞增順序排序
    @Query("SELECT * from item ORDER BY name ASC")
    fun getItems(): Flow<List<Item>>
}