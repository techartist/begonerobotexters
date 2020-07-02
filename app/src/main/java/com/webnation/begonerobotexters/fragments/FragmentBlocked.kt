package com.webnation.begonerobotexters.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
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

    override fun uncheckMainCheckbox() {
        checkbox.setOnCheckedChangeListener(null)
        checkbox.isChecked = false
        checkbox.setOnCheckedChangeListener(onCheckChangedListener)
    }

    val onCheckChangedListener by lazy {
        object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(checkbox: CompoundButton?, isChecked: Boolean) {
                adapter.checkAllBoxes(isChecked)
            }
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
        checkbox.setOnCheckedChangeListener(onCheckChangedListener)

        recyclerview.setAdapter(adapter)
        recyclerview.setLayoutManager(LinearLayoutManager(context))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!MainActivity.isAppAsDefaultDialer(requireContext())) {
            val alert = AlertDialog.Builder(requireContext())
            alert.setMessage(resources.getString(R.string.reason_for_default_dialer))
            alert.setTitle(resources.getString(R.string.not_default_dialer))

            alert.setNegativeButton(resources.getString(R.string.no)) { dialog, whichButton ->
                dialog.dismiss()
            }

            alert.setNegativeButton(resources.getString(R.string.yes)) { dialog, whichButton ->
                MainActivity.getPermissionToBeDefaultDialer(requireActivity() as MainActivity)
                dialog.dismiss()
            }

            alert.show()
        }

        if (context != null) {
            adapter = BlockedNumberRecyclerAdapter(context, this@FragmentBlocked)

            blockedNumberViewModel.allNumbers.observe(this, Observer {
                if (MainActivity.isAppAsDefaultDialer(requireContext())) {
                    if (it != null) {
                        adapter.setPhoneNumbers(it)
                    }
                    textView.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE
                    blockButton.visibility = View.GONE
                } else {
                    textView.visibility = View.VISIBLE
                    recyclerview.visibility = View.GONE
                    blockButton.visibility = View.VISIBLE
                    blockButton.setOnClickListener {
                        MainActivity.getPermissionToBeDefaultDialer(
                            requireActivity() as MainActivity
                        )
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