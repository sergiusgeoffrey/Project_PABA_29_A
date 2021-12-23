package com.example.chattale_chat

import android.os.Parcel
import android.os.Parcelable

data class Message(
    var messageID: String?,
    var chatroomID: String?,
    var fromUsername: String?,
    var timestamp: Long?,
    var message: String?,
    var isSystem: Boolean?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(messageID)
        parcel.writeString(chatroomID)
        parcel.writeString(fromUsername)
        parcel.writeValue(timestamp)
        parcel.writeString(message)
        parcel.writeByte(if (isSystem == true) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}