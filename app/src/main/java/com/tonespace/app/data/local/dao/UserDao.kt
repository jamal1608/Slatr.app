package com.tonespace.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tonespace.app.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM cached_users WHERE uid = :uid")
    suspend fun getUser(uid: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM cached_users WHERE uid = :uid")
    suspend fun deleteUser(uid: String)
}