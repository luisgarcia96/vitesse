package com.openclassrooms.vitesse.presentation.screens.candidates.add

import java.time.LocalDate

data class AddCandidateState(
  val firstName: String = "",
  val lastName: String = "",
  val phoneNumber: String = "",
  val email: String = "",
  val birthDate: LocalDate? = null,
  val expectedSalary: String = "",
  val notes: String = "",
  val photoUri: String? = null
)
