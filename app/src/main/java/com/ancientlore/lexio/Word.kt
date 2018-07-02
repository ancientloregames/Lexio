package com.ancientlore.lexio

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "words", indices = [(Index(value = "name", unique = true))])
data class Word(@PrimaryKey(autoGenerate = true) var id: Long = 0,
				@field:ColumnInfo(name = "name") var name: String = "",
				@field:ColumnInfo(name = "translation") var translation: String = "",
				@field:ColumnInfo(name = "transcription") var transcription: String = "") : Parcelable {

	private constructor(parcel: Parcel) : this(
			id = parcel.readValue(Long::class.java.classLoader) as Long,
			name = parcel.readString(),
			translation = parcel.readString(),
			transcription = parcel.readString())

	override fun writeToParcel(dest: Parcel, flags: Int) {
		dest.writeValue(id)
		dest.writeString(name)
		dest.writeString(translation)
		dest.writeString(transcription)
	}

	override fun describeContents() = 0

	companion object {
		@JvmField
		val CREATOR = object : Parcelable.Creator<Word> {
			override fun createFromParcel(parcel: Parcel) = Word(parcel)

			override fun newArray(size: Int) = arrayOfNulls<Word>(size)
		}
	}
}