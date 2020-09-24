package com.xzkj.test.capture

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.xzkj.test.capture.data.PhoneData

class PhoneNumber {

    fun getPhoneNumBerData(context: Context): ArrayList<PhoneData>? {
        val phoneList = ArrayList<PhoneData>()
        try {
            if (cursorList(context, phoneList)) return phoneList
        } catch (e: Exception) {
            e.fillInStackTrace()
        }
        return null
    }

    private fun cursorList(
        context: Context,
        phoneList: ArrayList<PhoneData>
    ): Boolean {
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone._ID + " desc"
        )
        if (getPhoneNum(cursor, phoneList)) return true
        return false
    }

    private fun getPhoneNum(
        cursor: Cursor?,
        phoneList: ArrayList<PhoneData>
    ): Boolean {
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val count =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED))
                val time =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                if (number != null && !number.isEmpty()) {
                    phoneList.add(PhoneData(name, number))
                }
            }
            return true
        }
        cursor?.close()
        return false
    }
}