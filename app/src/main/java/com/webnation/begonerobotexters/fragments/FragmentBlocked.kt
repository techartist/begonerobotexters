package com.webnation.begonerobotexters.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.webnation.begonerobotexters.MainActivity
import com.webnation.begonerobotexters.R
import com.webnation.begonerobotexters.adapters.BlockedNumberRecyclerAdapter
import com.webnation.begonerobotexters.database.PhoneNumber
import com.webnation.begonerobotexters.receivers.MMSBroadcastReceiver.context
import com.webnation.begonerobotexters.viewmodel.FragBlockedViewModel
import kotlinx.android.synthetic.main.fragment_blocked.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent


class FragmentBlocked : Fragment(), BlockedNumberRecyclerAdapter.OnItemChecked {

    override fun updatePhoneNumber(isBlocked: Boolean, phoneNumber: PhoneNumber) {

        blockedNumberViewModel.updatePhoneNumber(isBlocked, phoneNumber)
        if (isBlocked) {
            blockedNumberViewModel.blockNumberInSystem(phoneNumber.blockedNumber)
        } else {
            blockedNumberViewModel.deleteNumberInSystem(phoneNumber.blockedNumber)
        }

    }

    // Lazy Inject ViewModel
    val blockedNumberViewModel: FragBlockedViewModel by viewModel()
    lateinit var adapter : BlockedNumberRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_blocked, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkbox.setOnCheckedChangeListener{ checkbox, isChecked ->
            adapter.checkAllBoxes(isChecked)
        }

        recyclerview.setAdapter(adapter)
        recyclerview.setLayoutManager(LinearLayoutManager(context))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (context != null) {
            adapter = BlockedNumberRecyclerAdapter(context, this@FragmentBlocked)

            blockedNumberViewModel.allNumbers.observe(this, Observer {
                    if (MainActivity.isAppAsDefaultDialer(requireContext())) {
                        if (it != null) {
                            adapter.setPhoneNumbers(it)
                        }
                    }

            })
        }
    }


    companion object {
        fun getInstance() : FragmentBlocked {
            return FragmentBlocked()
        }

        val RC_DEFAULT_PHONE = 1234
    }

}