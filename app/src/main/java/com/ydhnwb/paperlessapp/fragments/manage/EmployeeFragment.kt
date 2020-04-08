package com.ydhnwb.paperlessapp.fragments.manage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.EmployeeAdapter
import com.ydhnwb.paperlessapp.viewmodels.EmployeeState
import com.ydhnwb.paperlessapp.viewmodels.EmployeeViewModel
import kotlinx.android.synthetic.main.fragment_employee.view.*

class EmployeeFragment : Fragment(R.layout.fragment_employee) {
    private lateinit var employeeViewModel: EmployeeViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rv_employee.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = EmployeeAdapter(mutableListOf(), activity!!)
        }
        employeeViewModel = ViewModelProvider(this).get(EmployeeViewModel::class.java)
        employeeViewModel.fetchAllEmployee()
        employeeViewModel.listenToUIState().observe(viewLifecycleOwner, Observer {
            handleUIState(it)
        })
        employeeViewModel.listenToEmployees().observe(viewLifecycleOwner, Observer {
            view.rv_employee.adapter?.let { adapter ->
                if(adapter is EmployeeAdapter){
                    adapter.updateList(it)
                }
            }
        })
    }

    private fun handleUIState(it: EmployeeState){
        when(it){
            is EmployeeState.IsLoading -> {
                if(it.state){ view!!.loading.visibility = View.VISIBLE }else{ view!!.loading.visibility = View.GONE }
            }
            is EmployeeState.ShowToast -> toast(it.message)
        }
    }

    private fun toast(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}