package com.ancientlore.lexio

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "words", indices = [(Index(value = "name", unique = true))])
data class Word(@PrimaryKey(autoGenerate = true) var id: Long = 0,
				@field:ColumnInfo(name = "name") var name: String = "",
				@field:ColumnInfo(name = "transcription") var transcription: String = "",
				@field:ColumnInfo(name = "translation") var translation: String = "")