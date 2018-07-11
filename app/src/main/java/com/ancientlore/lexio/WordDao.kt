package com.ancientlore.lexio

import android.arch.persistence.room.*

@Dao
interface WordDao {

	@Query("SELECT * FROM words")
	fun getAll(): List<Word>

	@Query("SELECT * FROM words WHERE id IN (:ids)")
	fun loadAllByIds(ids: IntArray): List<Word>

	@Query("SELECT * FROM words WHERE name LIKE :first")
	fun findByName(first: String): Word

	@Query("SELECT * FROM words WHERE topics LIKE '%' || :topic || '%'")
	fun findAllByTopic(topic: String): List<Word>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(vararg word: Word)

	@Insert
	fun insertAll(vararg word: Word)

	@Update
	fun update(vararg word: Word)

	@Delete
	fun delete(word: Word)
}