package com.webnation.begonerobotexters.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webnation.begonerobotexters.database.PhoneNumber


class BlockedNumberRecyclerAdapter internal constructor(val context: Context?, val onItemChecked: OnItemChecked) : RecyclerView.Adapter<BlockedNumberRecyclerAdapter.BlockedNumberViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    var phoneNumbers = arrayListOf<PhoneNumber>()
    private var allIsSelected = false
    private var mainCheckboxChecked = false

    interface OnItemChecked {
        fun updatePhoneNumber(isBlocked : Boolean, phoneNumber: PhoneNumber)
    }

    override fun getItemViewType(position :Int) : Int{
        when (position) {
            0 -> return 1
            else -> return 0
        }
    }

    override fun onBindViewHolder(holder: BlockedNumberViewHolder, position: Int) {
        if (phoneNumbers.size > 0) {
            val current = phoneNumbers[position]

            if (position > 0) {
                holder.checkBox.setOnCheckedChangeListener(null)
                holder.checkBox.isChecked = current.numberIsBlocked == 1

                if (mainCheckboxChecked) {
                    holder.checkBox.isChecked = allIsSelected
                }

                holder.checkBox.tag = current
                holder.phoneNumberTextView.text = current.blockedNumber
                holder.date.text = current.dateLastSeen
                holder.numberOfTimes.text = current.numberOfTimesSeen.toString()
                holder.checkBox.setOnCheckedChangeListener { checkbox, isChecked ->
                    onItemChecked.updatePhoneNumber(isChecked, checkbox.tag as PhoneNumber)
                }
                holder.checkBox.setOnClickListener{ mainCheckboxChecked = false }
            }
        }
    }

    override fun getItemCount(): Int {
        return phoneNumbers.size
    }

    fun checkAllBoxes(isChecked : Boolean) {
        allIsSelected = isChecked
        mainCheckboxChecked = true
        phoneNumbers.forEach {
            if (!it.blockedNumber.isEmpty()) {
                onItemChecked.updatePhoneNumber(isChecked, it)
            }
        }
        notifyDataSetChanged()
    }

    class BlockedNumberViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val phoneNumberTextView: TextView
        val checkBox : CheckBox
        val date : TextView
        val numberOfTimes: TextView

        init {
            phoneNumberTextView = itemView.findViewById(com.webnation.begonerobotexters.R.id.phoneNumber)
            val checkbox = itemView.findViewById<CheckBox>(com.webnation.begonerobotexters.R.id.checkbox)
            if (checkbox != null) checkBox =
                itemView.findViewById<CheckBox>(com.webnation.begonerobotexters.R.id.checkbox) else checkBox =
                CheckBox(itemView.context)
            date = itemView.findViewById(com.webnation.begonerobotexters.R.id.date)
            numberOfTimes = itemView.findViewById(com.webnation.begonerobotexters.R.id.numberOfTimesSeen)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedNumberViewHolder {
        val itemView : View
        if (viewType == 0) {
            itemView = mInflater.inflate(com.webnation.begonerobotexters.R.layout.recycler_item_view, parent, false)
        } else {
            itemView = mInflater.inflate(com.webnation.begonerobotexters.R.layout.recycler_item_header_view, parent, false)
        }
        return BlockedNumberViewHolder(itemView)
    }

    fun setPhoneNumbers(numbers: List<PhoneNumber>) {
        phoneNumbers = ArrayList(numbers)
        val phoneNumber = PhoneNumber("")
        phoneNumbers.add(0,phoneNumber)
        notifyDataSetChanged()
    }
}