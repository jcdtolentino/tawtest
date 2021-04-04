package com.example.tawktest.DataModels

import androidx.room.Entity

@Entity(tableName = "UserNote", primaryKeys = ["id"])
class UserNoteModel(
    val login: String,
    val id: Int,
    val note: String?
)