package com.ydhnwb.paperlessapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.adapters.NotificationAdapter
import com.ydhnwb.paperlessapp.viewmodels.NotificationState
import com.ydhnwb.paperlessapp.viewmodels.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_notifications.view.*

class NotificationFragment : Fragment(R.layout.fragment_notifications) {
    private lateinit var notificationViewModel: NotificationViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rv_notification.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = NotificationAdapter(mutableListOf(), activity!!)
        }
        notificationViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        notificationViewModel.fetchNotification()
        notificationViewModel.listenToUIState().observe(viewLifecycleOwner, Observer {
            when(it){
                is NotificationState.IsLoading -> isLoading(it.state)
                is NotificationState.ShowToast -> toast(it.message)
            }
        })
        notificationViewModel.listenToNotifications().observe(viewLifecycleOwner, Observer {
            view.rv_notification.adapter?.let { adapter ->
                if(adapter is NotificationAdapter){
                    adapter.updateList(it)
                }
            }
        })
    }

    private fun isLoading(state: Boolean) {
        if(state){
            view!!.loading_bar.visibility = View.VISIBLE
        }else{
            view!!.loading_bar.visibility = View.GONE
        }
    }

    private fun toast(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}