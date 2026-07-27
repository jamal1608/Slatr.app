package com.tonespace.app.di

import android.content.Context
import androidx.room.Room
import com.tonespace.app.data.local.ToneShareDatabase
import com.tonespace.app.data.local.dao.SoundDao
import com.tonespace.app.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ToneShareDatabase {
        return Room.databaseBuilder(
            context,
            ToneShareDatabase::class.java,
            "tonespace_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideSoundDao(db: ToneShareDatabase): SoundDao = db.soundDao()

    @Provides
    fun provideUserDao(db: ToneShareDatabase): UserDao = db.userDao()
}