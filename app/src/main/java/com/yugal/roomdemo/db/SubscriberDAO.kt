package com.yugal.roomdemo.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriberDAO {

    @Insert
    suspend fun insertSubscriber(subscriber: Subscriber): Long

    @Update
    suspend fun updateSubscriber(subscriber: Subscriber)

    @Delete
    suspend fun deleteSubscriber(subscriber: Subscriber)

    @Query("DELETE FROM subscriber_data_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM subscriber_data_table")
    fun getAllSubscribers():Flow<List<Subscriber>>
}