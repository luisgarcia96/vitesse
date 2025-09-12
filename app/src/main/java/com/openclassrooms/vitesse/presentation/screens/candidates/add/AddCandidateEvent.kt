package com.openclassrooms.vitesse.presentation.screens.candidates.add

import java.time.LocalDate

sealed interface AddCandidateEvent {
  object SaveCandidate: AddCandidateEvent
  object Cancel: AddCandidateEvent
  data class SetFirstName(val firstName: String): AddCandidateEvent
  data class SetLastName(val lastName: String): AddCandidateEvent
  data class SetPhoneNumber(val phoneNumber: String): AddCandidateEvent
  data class SetEmail(val email: String): AddCandidateEvent
  object showDatePicker: AddCandidateEvent
  object hideDatePicker: AddCandidateEvent
  data class SetBirthDate(val birthDate: LocalDate): AddCandidateEvent
  data class SetExpectedSalary(val expectedSalary: String): AddCandidateEvent
  data class SetNotes(val notes: String): AddCandidateEvent
  data class SetPhotoUri(val photoUri: String?): AddCandidateEvent
}
