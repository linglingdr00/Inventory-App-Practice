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

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Item
import com.example.inventory.databinding.FragmentAddItemBinding

/**
 * Fragment to add or update an item in the Inventory database.
 */
class AddItemFragment : Fragment() {

    // 建立可跨 fragments 使用的共用(shared) view model
    private val viewModel: InventoryViewModel by activityViewModels {
        // 呼叫 InventoryViewModelFactory() constructor 並傳入 ItemDao instance
        InventoryViewModelFactory(
            // 使用 application 的 database instance，呼叫 itemDao constructor
            (activity?.application as InventoryApplication).database.itemDao()
        )
    }

    // 建立 type 為 Item 的 item
    lateinit var item: Item

    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // bind fragment_add_item.xml layout
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 覆寫 onViewCreated() function
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 新增 navigation 引數 itemId 為 id
        val id = navigationArgs.itemId

        // 如果 id 大於 0 代表 item 已建立
        if (id > 0) {
            // 使用 id 擷取 entity，然後在其中新增 observer
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) {
                // 如果 data 改變，則更新 item 屬性並呼叫 bind() 更新 UI
                selectedItem ->
                item = selectedItem
                bind(item)
            }
        } else { // // 如果 id = 0 代表 item 尚未建立
            // 為 save button 新增 click listener
            binding.saveAction.setOnClickListener {
                // 點擊 save button，則新增 new item
                addNewItem()
            }
        }

    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    // 實作 isEntryValid function，驗證使用者輸入內容
    private fun isEntryValid():Boolean {
        // 將 TextFields 中的 text 轉成 string 傳給 view model 的 isEntryValid function
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.itemPrice.text.toString(),
            binding.itemCount.text.toString()
        )
    }

    private fun addNewItem() {
        // 如果 text 都不為空
        if (isEntryValid()) {
            // 呼叫 view model 的 addNewItem function 來新增 item
            viewModel.addNewItem(
                binding.itemName.text.toString(),
                binding.itemPrice.text.toString(),
                binding.itemCount.text.toString()
            )
        }
        // 導覽回 ItemListFragment
        val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
        findNavController().navigate(action)
    }

    // 新增 bind() 更新 UI
    private fun bind(item: Item) {
        // 使用 format() 函式將 price 四捨五入至小數點後兩位
        val price = "%.2f".format(item.itemPrice)

        binding.apply {
            // 將 text fields 與 entity details bind 在一起
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            itemPrice.setText(price, TextView.BufferType.SPANNABLE)
            itemCount.setText(item.quantityInStock.toString(), TextView.BufferType.SPANNABLE)

            // 設定 save button 的 click listener
            saveAction.setOnClickListener {
                updateItem()
            }
        }

    }

    private fun updateItem() {
        // 呼叫 isEntryValid() 驗證使用者輸入內容
        if (isEntryValid()) {
            // 呼叫傳遞 entity details 的 viewModel.updateItem()
            viewModel.updateItem(
                this.navigationArgs.itemId,
                this.binding.itemName.text.toString(),
                this.binding.itemPrice.text.toString(),
                this.binding.itemCount.text.toString()
            )
            // navigate 至 ItemListFragment
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }
}
