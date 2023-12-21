package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// database class
// 建立 item database class
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class ItemRoomDatabase: RoomDatabase() {
    // return ItemDao 的 abstract function
    abstract fun itemDao(): ItemDao

    // 定義 companion object (可使用 class name 做為限定詞，建立或取得 database)
    companion object {
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        // 使用 database builder 所需的 Context 參數定義 getDatabase() method
        fun getDatabase(context: Context): ItemRoomDatabase {
            return INSTANCE ?: synchronized(this) { // synchronized區塊中，一次只能執行一個 thread
                // 使用 database builder 取得 database instance
                // 將 application context、database class 以及 database name item_database 傳遞給 database builder
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_database")
                    .fallbackToDestructiveMigration() // 將遷移策略新增至 builder
                    .build()
                INSTANCE = instance // 將 INSTANCE 設為剛才建立好的 instance
                return instance
            }
        }
    }
}