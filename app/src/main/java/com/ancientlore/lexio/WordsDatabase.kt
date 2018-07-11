package com.ancientlore.lexio

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = [(Word::class), (Topic::class)], version = 1)
@TypeConverters(DataConverters::class)
abstract class WordsDatabase : RoomDatabase() {

	abstract fun wordDao(): WordDao

	abstract fun topicDao(): TopicDao

	companion object : SingletonHolder<WordsDatabase, Context>({
		Room.databaseBuilder(it,
				WordsDatabase::class.java, "words.db")
				.build()
	})
}