/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Item
import com.example.inventory.data.getFormatPrice
import com.example.inventory.databinding.FragmentItemDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * [ItemDetailFragment] displays the details of the selected item.
 */
class ItemDetailFragment : Fragment() {
    // 建立 ViewModel instance
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao()
        )
    }

    // 新增 item，儲存單一 entity 的相關資訊
    lateinit var item: Item

    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    // 新增 bind() 更新 UI
    private fun bind(item: Item) {
        binding.apply {
            itemName.text = item.itemName
            itemPrice.text = item.getFormatPrice()
            itemCount.text = item.quantityInStock.toString()

            // 當 item 庫存大於 0 時才 enable sell button
            sellItem.isEnabled =  viewModel.isStockAvailable(item)
            // 設定 sell buton 的 click listener
            sellItem.setOnClickListener {
                // 呼叫 view model 中的 sellItem()，傳入 item object(entity)
                viewModel.sellItem(item)
            }

            // 設定 Delete button 的 click listener
            deleteItem.setOnClickListener {
                // 顯示是否確認刪除的 dialog
                showConfirmationDialog()
            }

            // 設定 Edit button 的 click listener
            editItem.setOnClickListener {
                // 呼叫 editItem()，前往「Edit Item」畫面
                editItem()
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 新增傳入的 navigation 引數 itemId 為 id
        val id = navigationArgs.itemId
        // 傳入 id 的 viewModel 呼叫 retrieveItem()
        // 將 observer 附加至傳入 viewLifecycleOwner 和 lambda 的 return value
        viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) {
            // 傳入 selectedItem 做為參數
            selectedItem ->
            // 將 selectedItem value 指派給 item
            item = selectedItem
            // 呼叫傳入 item 的 bind()
            bind((item))
        }
    }

    // 編輯 item 的屬性(欄位)資訊
    private fun editItem() {
        // 將畫面標題更新為「Edit Item」，以重複使用 fragment_add_item.xml
        val action = ItemDetailFragmentDirections.actionItemDetailFragmentToAddItemFragment(
            // 傳入參數 fragment title string 以及 item id
            getString(R.string.edit_fragment_title),
            item.id
        )
        // 前往「Edit Item」畫面
        this.findNavController().navigate(action)
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     * 在刪除 item 前取得使用者的確認，並在使用者點選 positive button 時呼叫 deleteItem()
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem()
            }
            .show()
    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteItem() {
        // 呼叫 view model 中的 deleteItem()，刪除 database 中的 item
        viewModel.deleteItem(item)
        findNavController().navigateUp()
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
