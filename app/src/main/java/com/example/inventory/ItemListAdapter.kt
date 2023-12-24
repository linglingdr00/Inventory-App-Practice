package com.example.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.data.Item
import com.example.inventory.data.getFormatPrice
import com.example.inventory.databinding.ItemListItemBinding

class ItemListAdapter(private val onItemClicked: (Item) -> Unit):
    ListAdapter<Item, ItemListAdapter.ItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.itemName == newItem.itemName
            }

        }
    }

    // 建立 view holder，繼承 RecyclerView.ViewHolder
    class ItemViewHolder(private val binding: ItemListItemBinding):
        RecyclerView.ViewHolder(binding.root) {
            // 覆寫 bind()，傳入 Item object
            fun bind(item: Item) {
                // 設定 bind object
                binding.apply {
                    itemName.text = item.itemName
                    itemPrice.text = item.getFormatPrice()
                    itemQuantity.text = item.quantityInStock.toString()
                }
            }
    }

    // 覆寫 onCreateViewHolder，在 RecyclerView 需要時 return 新的 ViewHolder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        // 建立一個新的 View
        return ItemViewHolder(
            // 從 item_list_item.xml layout 中加載
            ItemListItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    // 覆寫 onBindViewHolder
    override fun onBindViewHolder(holder: ItemListAdapter.ItemViewHolder, position: Int) {
        // 取得 item 目前的 position
        val current = getItem(position)
        // 在 itemView 上設定 click listener
        holder.itemView.setOnClickListener {
            // 呼叫 function onItemClicked()
            onItemClicked(current)
        }
        holder.bind(current)
    }
}