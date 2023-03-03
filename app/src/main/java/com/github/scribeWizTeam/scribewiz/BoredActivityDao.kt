package com.github.scribeWizTeam.scribewiz
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BoredActivityDao {
    @Query("SELECT * FROM boredActivity")
    fun getAll(): List<BoredActivity>

    @Insert
    fun insert(vararg users: BoredActivity)

}