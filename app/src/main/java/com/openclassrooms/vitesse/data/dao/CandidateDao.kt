package com.openclassrooms.vitesse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.openclassrooms.vitesse.data.entity.Candidate
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {

  @Upsert
  suspend fun upsertCandidate(candidate: Candidate)

  @Query("SELECT * FROM candidate")
  fun getAllCandidates(): Flow<List<Candidate>> //Add suspend later

  @Query("SELECT * FROM candidate WHERE id = :id")
  fun getCandidateById(id: Int): Candidate? //Add suspend later

  @Delete
  fun deleteCandidate(candidate: Candidate) //Add suspend later
}