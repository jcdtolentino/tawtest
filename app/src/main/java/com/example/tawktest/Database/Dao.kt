package com.example.tawktest.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tawktest.DataModels.UserDetailModel
import com.example.tawktest.DataModels.UserModel
import com.example.tawktest.DataModels.UserNoteModel

@Dao
interface Dao  {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    fun insertUsers(organizationList: List<UserModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    fun insertUserDetail(userDetail: UserDetailModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserNote(userNoteModel: UserNoteModel)

    @Query("SELECT * FROM UserList")
    fun getAllUsers(): List<UserModel>

    @Query("SELECT * FROM UserDetail WHERE login = :login")
    fun getUserDetail(login: String): UserDetailModel

    @Query("SELECT * FROM UserNote WHERE login = :login")
    fun getUserNote(login: String): UserNoteModel

    @Query("SELECT * FROM UserNote")
    fun getUsersNote(): LiveData<List<UserNoteModel>>

    @Query("DELETE FROM UserList")
    fun deleteALLUsers()

    @Query("DELETE FROM UserDetail")
    fun deleteUserDetail()

    @Query("DELETE FROM UserNote WHERE login = :login")
    fun deleteUserNote(login: String)
}