package com.example.chattale_chat

import android.os.Parcel
import android.os.Parcelable

data class Chatroom(
    var chatroomID: String?,
    var displayName: String?,
    var members: List<String>?,
    var lastMessage: Message?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readParcelable(Message::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chatroomID)
        parcel.writeString(displayName)
        parcel.writeStringList(members)
        parcel.writeParcelable(lastMessage, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chatroom> {
        override fun createFromParcel(parcel: Parcel): Chatroom {
            return Chatroom(parcel)
        }

        override fun newArray(size: Int): Array<Chatroom?> {
            return arrayOfNulls(size)
        }
    }
}