package com.webnation.begonerobotexters.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_numbers")
class PhoneNumber(@field:PrimaryKey
                  @field:ColumnInfo(name="blocked_number")
           val blockedNumber: String) {

    @field:ColumnInfo(name="number_of_times_seen")
    var numberOfTimesSeen : Int = 0

    @field:ColumnInfo(name="last_date_seen")
    var dateLastSeen: String = ""

    @field:ColumnInfo(name="number_is_blocked")
    var numberIsBlocked : Int = 0
}
