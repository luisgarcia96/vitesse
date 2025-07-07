package com.openclassrooms.vitesse.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Candidate(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,

  val firstName: String,
  val lastName: String,
  val phoneNumber: String,
  val email: String,
  val birthDate: LocalDate,
  val expectedSalary: Int,
  val notes: String
)