package com.example.tawktest.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tawktest.DataModels.UserDetailModel
import com.example.tawktest.DataModels.UserModel
import com.example.tawktest.DataModels.UserNoteModel


@Database(
    entities =[UserModel::class, UserDetailModel::class, UserNoteModel::class
    ], exportSchema = false, version = 1
)
abstract class LocalDb :  RoomDatabase() {
    abstract fun getDao() : Dao
}