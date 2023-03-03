package com.github.scribeWizTeam.scribewiz
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BoredActivity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boredActivityDao(): BoredActivityDao
}
