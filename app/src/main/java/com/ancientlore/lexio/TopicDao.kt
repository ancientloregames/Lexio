package com.ancientlore.lexio

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface TopicDao {

	@Query("SELECT * FROM topics")
	fun getAll(): List<Topic>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(vararg topic: Topic)
}