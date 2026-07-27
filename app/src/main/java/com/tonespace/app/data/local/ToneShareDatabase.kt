package com.tonespace.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tonespace.app.data.local.dao.SoundDao
import com.tonespace.app.data.local.dao.UserDao
import com.tonespace.app.data.local.entity.SoundEntity
import com.tonespace.app.data.local.entity.UserEntity

@Database(
    entities = [SoundEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ToneShareDatabase : RoomDatabase() {
    abstract fun soundDao(): SoundDao
    abstract fun userDao(): UserDao
}