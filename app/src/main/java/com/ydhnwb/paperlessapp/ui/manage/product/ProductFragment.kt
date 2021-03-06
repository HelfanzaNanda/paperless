package com.ydhnwb.paperlessapp.ui.manage.product

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.manage.ManageStoreViewModel
import com.ydhnwb.paperlessapp.ui.product.ProductActivity
import com.ydhnwb.paperlessapp.models.Product
import com.ydhnwb.paperlessapp.utilities.PaperlessUtil
import com.ydhnwb.paperlessapp.utilities.extensions.showInfoAlert
import kotlinx.android.synthetic.main.fragment_product.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductFragment : Fragment(R.layout.fragment_product), ProductAdapterClick {
    private val productViewModel: ProductViewModel by viewModel()
    private val parentStoreViewModel: ManageStoreViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        checkRole()
        observe()
    }

    private fun setupRecyclerView(){
        requireView().rv_manage_product.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = DetailedProductAdapter(mutableListOf(), this@ProductFragment)
        }
    }

    private fun checkRole(){
        if(getRole() == 0){
            requireView().fab_add.hide()
        }else{
            requireView().fab_add.show()
            requireView().fab_add.setOnClickListener {
                startActivity(Intent(activity, ProductActivity::class.java).apply {
                    putExtra("STORE", parentStoreViewModel.listenToCurrentStore().value!!)
                })
            }
        }
    }

    private fun observe(){
        observeState()
        observeProducts()
    }

    private fun observeState() = productViewModel.listenToUIState().observer(viewLifecycleOwner, Observer { handleUIState(it) })
    private fun observeProducts() = productViewModel.listenToProducts().observe(viewLifecycleOwner, Observer { handleData(it) })

    override fun onResume() {
        super.onResume()
        PaperlessUtil.getToken(activity!!)?.let { productViewModel.fetchProducts(it, parentStoreViewModel.listenToCurrentStore().value!!.id.toString()) }
    }

    private fun handleData(it: List<Product>){
        with(requireView()){
            if(it.isNullOrEmpty()){
                empty_view.visibility = View.VISIBLE
            }else{
                empty_view.visibility = View.GONE
            }
            rv_manage_product.adapter?.let { i ->
                if(i is DetailedProductAdapter){
                    i.updateList(it.sortedBy { product -> product.name })
                }
            }
            if(productViewModel.listenToProducts().value == null || productViewModel.listenToProducts().value!!.isEmpty()){
                empty_view.visibility = View.VISIBLE
            }else{
                empty_view.visibility = View.GONE
            }
        }

    }

    private fun toast(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    private fun handleUIState(it: ProductState){
        when(it){
            is ProductState.ShowToast -> toast(it.message)
            is ProductState.IsLoading -> {
                if(it.state){ view?.loading?.visibility = View.VISIBLE }else{ view?.loading?.visibility = View.GONE }
            }
        }
    }

    private fun getRole() = requireActivity().intent.getIntExtra("ROLE", -1)
    override fun changeAvailibility(currentProduct: Product) {
        PaperlessUtil.getToken(requireActivity())?.let {
            productViewModel.changeAvailibilityOfProduct(
                it, parentStoreViewModel.listenToCurrentStore().value?.id.toString(),
                currentProduct)
        }
    }

    override fun click(product: Product) {
        if (getRole() != 0){
            requireActivity().startActivity(Intent(context, ProductActivity::class.java).apply {
                putExtra("PRODUCT", product)
                putExtra("STORE",parentStoreViewModel.listenToCurrentStore().value)
            })
        }else{
            requireActivity().showInfoAlert(resources.getString(R.string.permission_not_allowed))
        }
    }
}