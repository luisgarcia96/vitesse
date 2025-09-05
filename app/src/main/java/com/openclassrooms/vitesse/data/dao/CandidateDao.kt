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

  @Query("SELECT * FROM candidate WHERE isFavorite = 1")
  fun getFavoriteCandidates(): Flow<List<Candidate>>

  @Query("SELECT * FROM candidate WHERE id = :id")
  suspend fun getCandidateById(id: Int): Candidate? //Add suspend later

  @Query("UPDATE candidate SET isFavorite = :isFavorite WHERE id = :id")
  suspend fun setFavorite(id: Int, isFavorite: Boolean)

  @Delete
  suspend fun deleteCandidate(candidate: Candidate) //Add suspend later
}
