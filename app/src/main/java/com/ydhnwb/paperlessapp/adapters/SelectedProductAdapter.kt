package com.ydhnwb.paperlessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.component_number_picker.view.*
import kotlinx.android.synthetic.main.list_item_selected_product.view.*

class SelectedProductAdapter (private var selectedProducts : MutableList<Product>, private var context: Context, private var productViewModel : ProductViewModel)
    : RecyclerView.Adapter<SelectedProductAdapter.ViewHolder>(){

    fun updateList(sps : List<Product>){
        selectedProducts.clear()
        selectedProducts.addAll(sps)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_selected_product, parent, false))

    override fun getItemCount() = selectedProducts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(selectedProducts[position], context, productViewModel)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, context: Context, productViewModel: ProductViewModel){
            itemView.product_incrementQuantity.setOnClickListener { productViewModel.incrementQuantity(product) }
            itemView.product_decrementQuantity.setOnClickListener { productViewModel.decrementQuantity(product) }
            itemView.product_name.text = product.name
            itemView.product_price.text = PaperlessUtil.setToIDR(product.price!!*product.selectedQuantity!!)
            itemView.product_image.load(product.image)
            itemView.product_selectedQuantity.text = product.selectedQuantity.toString()
            itemView.product_more.setOnClickListener {
                PopupMenu(context, it).apply {
                    menuInflater.inflate(R.menu.menu_common_selected_product, menu)
                    setOnMenuItemClickListener { menuItems ->
                        when(menuItems.itemId){
                            R.id.menu_delete -> {
                                productViewModel.deleteSelectedProduct(product)
                                true
                            }
                            else -> true
                        }
                    }
                }.show()
            }
            itemView.setOnClickListener {
            }
        }
    }
}