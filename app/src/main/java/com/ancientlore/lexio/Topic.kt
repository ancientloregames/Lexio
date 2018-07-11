package com.ancientlore.lexio

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "topics")
class Topic(@PrimaryKey var name: String = ""): Parcelable {

	constructor(parcel: Parcel) : this(parcel.readString())

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(name)
	}

	override fun describeContents() = 0

	companion object CREATOR : Parcelable.Creator<Topic> {
		override fun createFromParcel(parcel: Parcel): Topic {
			return Topic(parcel)
		}

		override fun newArray(size: Int): Array<Topic?> {
			return arrayOfNulls(size)
		}
	}
}