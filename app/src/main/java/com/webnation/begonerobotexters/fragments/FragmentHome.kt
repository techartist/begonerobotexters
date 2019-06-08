package com.webnation.begonerobotexters.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.webnation.begonerobotexters.R
import com.webnation.begonerobotexters.viewmodel.FragHomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class FragmentHome : Fragment() {

    // Lazy Inject ViewModel
    val fragHomeViewModel: FragHomeViewModel by viewModel()

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            fragHomeViewModel.putString( s.toString())
            Timber.d(fragHomeViewModel.getString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var frag = inflater.inflate(R.layout.fragment_home, container, false)
        val editText = frag.findViewById<EditText>(R.id.editText)
        val tb = frag.findViewById<ToggleButton>(R.id.tb)

        if (fragHomeViewModel.doesSharedPrefsHaveAutoResponder()) {
            editText.setText( fragHomeViewModel.getString())
        }
        editText.addTextChangedListener(textWatcher)


        if (fragHomeViewModel.doesSharedPrefsContainAutoResponder()) {
            tb?.isChecked = fragHomeViewModel.getBoolean()
        }

        tb.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.background = if (isChecked) ContextCompat.getDrawable(requireContext(),R.drawable.ic_toggle_switch_on)
            else  ContextCompat.getDrawable(requireContext(),R.drawable.ic_toggle_switch_off)
            fragHomeViewModel.putBoolean(isChecked)
        }
        return frag
    }
    companion object{
        fun getInstance() : FragmentHome {
            return FragmentHome()
        }
    }
}