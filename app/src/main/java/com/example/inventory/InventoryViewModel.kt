package com.example.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

// view model
// ViewModel 將透過 DAO 與 database 互動，並將 data 提供給 UI
class InventoryViewModel(private val itemDao: ItemDao): ViewModel() {
    // 抓取 Item object，將其加入 database
    private fun insertItem(item: Item) {
        viewModelScope.launch { // 啟動 coroutine
            // 透過 dao 的 insert 方法，新增 item 到 database
            itemDao.insert(item)
        }

    }

    // 抓取三個 strings，並 return Item instance
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(), // 轉成 Double
            quantityInStock = itemCount.toInt() // 轉成 Int
        )
    }

    // 將 new entity 新增至 database
    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
        // 將 newItem 轉換成 Item object
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        // 將 Item object 傳給 insertItem function
        insertItem(newItem)
    }

    // 驗證使用者輸入內容 (驗證 TextFields 中的 text 非 empty)
    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

}

// 對 InventoryViewModel instance 執行實例化(instantiate)
class InventoryViewModelFactory(private val itemDao: ItemDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 檢查 modelClass 是否和 InventoryViewModel class 相同
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            //  return 一個 instance
            return InventoryViewModel(itemDao) as T
        }
        // throw exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}