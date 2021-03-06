package com.ydhnwb.paperlessapp.ui.manage.employee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.models.Employee
import kotlinx.android.synthetic.main.list_item_employee.view.*

class EmployeeAdapter (private val employees: MutableList<Employee>, private val employeeInterface: EmployeeInterface) : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>(){

    fun updateList(e: List<Employee>){
        employees.clear()
        employees.addAll(e)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_employee, parent, false))

    override fun getItemCount() = employees.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(employees[position])

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(employee: Employee){
            with(itemView){
                employee_image.load(employee.user?.image)
                employee_name.text = employee.user!!.name
                employee_role.text = if(employee.role == 0) resources.getString(R.string.cashier) else resources.getString(R.string.staff)
                employee_more.setOnClickListener {
                    employeeInterface.moreClick(employee, it)
                }
                setOnClickListener {
                    employeeInterface.click(employee)
                }
            }
        }
    }
}