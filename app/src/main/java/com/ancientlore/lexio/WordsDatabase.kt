package com.ancientlore.lexio

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.ancientlore.lexio.WordsDatabase.Companion.MIGRATION_1_2

@Database(entities = [(Word::class), (Topic::class)], version = 2)
@TypeConverters(DataConverters::class)
abstract class WordsDatabase : RoomDatabase() {

	abstract fun wordDao(): WordDao

	abstract fun topicDao(): TopicDao

	companion object : SingletonHolder<WordsDatabase, Context>({
		Room.databaseBuilder(it,
				WordsDatabase::class.java, "words.db")
				.addMigrations(MIGRATION_1_2)
				.build()
	}) {
		@JvmField val MIGRATION_1_2: Migration = object : Migration(1, 2) {
			override fun migrate(database: SupportSQLiteDatabase) {
				database.execSQL("ALTER TABLE words ADD COLUMN note TEXT NOT NULL DEFAULT \"\";")
			}
		}
	}
}