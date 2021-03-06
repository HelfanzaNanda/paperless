package com.ydhnwb.paperlessapp.repositories

import com.ydhnwb.paperlessapp.models.Employee
import com.ydhnwb.paperlessapp.models.EmployeeResponse
import com.ydhnwb.paperlessapp.models.Store
import com.ydhnwb.paperlessapp.models.User
import com.ydhnwb.paperlessapp.utilities.ArrayResponse
import com.ydhnwb.paperlessapp.utilities.SingleResponse
import com.ydhnwb.paperlessapp.utilities.WrappedListResponse
import com.ydhnwb.paperlessapp.utilities.WrappedResponse
import com.ydhnwb.paperlessapp.webservices.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface EmployeeContract {
    fun updateEmployeeRole(token: String, storeId: String, role : Int, userId: Int, listener: SingleResponse<Employee>)
    fun getEmployees(token: String, storeId: String, listener: ArrayResponse<Employee>)
    fun removeEmployee(token: String, storeId: String, employeeId: String, listener: SingleResponse<Store>)
}

class EmployeeRepository (private val api: ApiService) : EmployeeContract{
    override fun updateEmployeeRole(token: String, storeId: String, role: Int, userId: Int, listener: SingleResponse<Employee>) {
        api.update_employee_role(token, storeId, role, userId).enqueue(object : Callback<WrappedResponse<Employee>>{
            override fun onFailure(call: Call<WrappedResponse<Employee>>, t: Throwable) {
                println(t.message)
                listener.onFailure(Error(t.message))
            }
            override fun onResponse(call: Call<WrappedResponse<Employee>>, response: Response<WrappedResponse<Employee>>) {
                when{
                    response.isSuccessful -> {
                        val res = response.body()!!
                        if(res.status) listener.onSuccess(res.data) else listener.onFailure(Error(res.message))
                    }
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun getEmployees(token: String, storeId: String, listener: ArrayResponse<Employee>) {
        api.store_employee(token, storeId).enqueue(object:
            Callback<WrappedListResponse<Employee>> {
            override fun onFailure(call: Call<WrappedListResponse<Employee>>, t: Throwable) = listener.onFailure(Error(t.message))

            override fun onResponse(call: Call<WrappedListResponse<Employee>>, response: Response<WrappedListResponse<Employee>>) {
                when{
                    response.isSuccessful -> listener.onSuccess(response.body()!!.data)
                    else -> listener.onFailure(Error(response.message()))
                }
            }
        })
    }

    override fun removeEmployee(token: String, storeId: String, employeeId: String, listener: SingleResponse<Store>) {
        api.employee_remove(token, storeId, employeeId).enqueue(object: Callback<WrappedResponse<Store>>{
            override fun onFailure(call: Call<WrappedResponse<Store>>, t: Throwable) {
                println(t.message)
                listener.onFailure(Error(t.message))
            }

            override fun onResponse(call: Call<WrappedResponse<Store>>, response: Response<WrappedResponse<Store>>) {
                if(response.isSuccessful){
                    val b = response.body()
                    if(b!!.status){
                        listener.onSuccess(b.data)
                    }else{
                        listener.onFailure(Error(b.message))
                    }
                }else{
                    listener.onFailure(Error(response.message()))
                }
            }
        })
    }
}