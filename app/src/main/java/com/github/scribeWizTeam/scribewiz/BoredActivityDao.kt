package com.github.scribeWizTeam.scribewiz
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface BoredActivityDao {
    @Query("SELECT * FROM boredActivity")
    suspend fun getAll(): List<BoredActivity>

    @Insert(onConflict = REPLACE)
    fun insertAll(vararg users: BoredActivity)

}