package com.openclassrooms.vitesse.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.openclassrooms.vitesse.data.converter.LocalDateConverter
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.data.entity.Candidate

@Database(
  entities = [Candidate::class],
  version = 1,
  exportSchema = false
)

@TypeConverters(LocalDateConverter::class)
abstract class CandidateDatabase: RoomDatabase() {
  abstract val dao: CandidateDao

}