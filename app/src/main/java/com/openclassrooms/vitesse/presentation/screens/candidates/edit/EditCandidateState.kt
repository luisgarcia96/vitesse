package com.openclassrooms.vitesse.presentation.screens.candidates.edit

import java.time.LocalDate

data class EditCandidateState(
  val id: Int = 0,
  val firstName: String = "",
  val lastName: String = "",
  val phoneNumber: String = "",
  val email: String = "",
  val birthDate: LocalDate? = null,
  val expectedSalary: String = "",
  val notes: String = ""
)

