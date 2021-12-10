package dev.bahodir.uploadgalleryeasy.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dev.bahodir.uploadgalleryeasy.user.User

class DBHelper(var context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), DB {

    companion object {
        const val DATABASE_NAME = "helper.db"
        const val DATABASE_VERSION = 1
        const val REGISTER = "register"
        const val ID = "id"
        const val NAME = "name"
        const val NUMBER = "number"
        const val COUNTRY = "country"
        const val ADDRESS = "address"
        const val PASSWORD = "password"
        const val PHOTO = "photo"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE $REGISTER($ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, $NAME TEXT NOT NULL, $NUMBER TEXT NOT NULL, $COUNTRY TEXT NOT NULL, $ADDRESS TEXT NOT NULL, $PASSWORD TEXT NOT NULL, $PHOTO TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    override fun add(user: User) {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(NAME, user.name)
        contentValues.put(NUMBER, user.number)
        contentValues.put(COUNTRY, user.country)
        contentValues.put(COUNTRY, user.country)
        contentValues.put(ADDRESS, user.address)
        contentValues.put(PASSWORD, user.password)
        contentValues.put(PHOTO, user.photo.toString())

        db.insert(REGISTER, null, contentValues)
        db.close()
    }

    override fun edit(user: User) {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(ID, user.id)
        contentValues.put(NAME, user.name)
        contentValues.put(NUMBER, user.number)
        contentValues.put(COUNTRY, user.country)
        contentValues.put(COUNTRY, user.country)
        contentValues.put(ADDRESS, user.address)
        contentValues.put(PASSWORD, user.password)
        contentValues.put(PHOTO, user.photo.toString())

        db.update(REGISTER, contentValues, "$ID = ?", arrayOf(user.id.toString()))
        db.close()
    }

    override fun delete(user: User) {
        val db = this.writableDatabase
        db.delete(REGISTER, "$ID = ?", arrayOf(user.id.toString()))
        db.close()
    }

    override fun getList(): MutableList<User> {
        val list = mutableListOf<User>()
        val cursor = this.readableDatabase.rawQuery("SELECT * FROM $REGISTER", null)

        if (cursor.moveToFirst()) {

            do {

                list.add(
                    User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                    )
                )

            } while (cursor.moveToNext())
        }
        return list
    }

}