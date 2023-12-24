package com.example.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

// view model
// ViewModel 將透過 DAO 與 database 互動，並將 data 提供給 UI
class InventoryViewModel(private val itemDao: ItemDao): ViewModel() {

    // 取得 database 中的所有 item
    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    // 根據 item id 從 database 中擷取 item details
    fun retrieveItem(id: Int): LiveData<Item> {
        // 對 itemDao 呼叫 getItem() 並傳入 id，將 Flow value 做為 LiveData
        return itemDao.getItem(id).asLiveData()
    }

    // 新增 Item object(entity) 到 database 中
    private fun insertItem(item: Item) {
        viewModelScope.launch { // 啟動 coroutine
            // 透過 dao 的 insert 方法，新增 item 到 database
            itemDao.insert(item)
        }
    }

    // 更新 database 中的 Item object(entity)
    fun updateItem(item: Item) {
        viewModelScope.launch { // 啟動 coroutine
            // 透過 dao 的 update 方法，更新 database 的 item
            itemDao.update(item)
        }
    }

    // 刪除 database 中的 Item object(entity)
    fun deleteItem(item: Item) {
        viewModelScope.launch { // 啟動 coroutine
            // 透過 dao 的 delete 方法，刪除 database 的 item
            itemDao.delete(item)
        }
    }

    fun sellItem(item: Item) {
        // 檢查 item 庫存是否大於 0
        if (item.quantityInStock > 0) {
            // copy() 此 item，將 item 庫存數量 -1
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            // 將更新好的 new item 傳遞到 updateItem()，以傳遞更新到 database
            updateItem(newItem)
        }
    }

    // 在沒有 item 可銷售時停用 Sell button
    fun isStockAvailable(item: Item): Boolean {
        // 如果 item 庫存大於 0 return true，小於 0 return false
        return (item.quantityInStock > 0)
    }

    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String) {
        // 呼叫 getUpdatedItemEntry()，取得 Item object
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
        // 將 Item object 傳入 updateItem(item: Item) 以更新 database
        updateItem(updatedItem)
    }

    // 設定要更新到 database 中的 Item object
    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String): Item {
        // 將 data 轉成要更新到 database 的 Item object
        return Item(
            id = itemId,
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
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