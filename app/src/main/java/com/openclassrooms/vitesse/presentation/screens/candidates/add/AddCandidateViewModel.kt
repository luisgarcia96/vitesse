package com.openclassrooms.vitesse.presentation.screens.candidates.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.data.entity.Candidate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddCandidateViewModel(
  private val dao: CandidateDao
): ViewModel() {

  private val _state = MutableStateFlow(AddCandidateState())
  val state: StateFlow<AddCandidateState> = _state.asStateFlow()

  fun onEvent(event: AddCandidateEvent) {
    when (event) {
      AddCandidateEvent.Cancel -> TODO()

      AddCandidateEvent.SaveCandidate -> {
        val firstName = _state.value.firstName
        val lastName = _state.value.lastName
        val phoneNumber = _state.value.phoneNumber
        val email = _state.value.email
        val birthDate = _state.value.birthDate
        val expectedSalary = _state.value.expectedSalary
        val notes = _state.value.notes

        if (firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank() || email.isBlank() || birthDate == null || expectedSalary.isBlank()) {
          return
        }

        val candidate = Candidate(
          firstName = firstName,
          lastName = lastName,
          phoneNumber = phoneNumber,
          email = email,
          birthDate = birthDate,
          expectedSalary = expectedSalary.toInt(),
          notes = notes
        )

        // Save candidate to database
        viewModelScope.launch {
          dao.upsertCandidate(candidate)
        }

        // Reset fields after saving
        _state.update {
          it.copy(
            firstName = "",
            lastName = "",
            phoneNumber = "",
            email = "",
            birthDate = null,
            expectedSalary = "",
            notes = ""
          )
        }
      }
      is AddCandidateEvent.SetBirthDate -> {
        _state.update {
          it.copy(
            birthDate = event.birthDate
          )
        }
      }
      is AddCandidateEvent.SetEmail -> {
        _state.update {
          it.copy(
            email = event.email
          )
        }
      }
      is AddCandidateEvent.SetExpectedSalary -> {
        _state.update {
          it.copy(
            expectedSalary = event.expectedSalary
          )
        }
      }
      is AddCandidateEvent.SetFirstName -> {
        _state.update {
          it.copy(
            firstName = event.firstName
          )
        }
      }
      is AddCandidateEvent.SetLastName -> {
        _state.update {
          it.copy(
            lastName = event.lastName
          )
        }
      }
      is AddCandidateEvent.SetNotes -> {
        _state.update {
          it.copy(
            notes = event.notes
          )
        }
      }
      is AddCandidateEvent.SetPhoneNumber -> {
        _state.update {
          it.copy(
            phoneNumber = event.phoneNumber
          )
        }
      }
      AddCandidateEvent.hideDatePicker -> {}
      AddCandidateEvent.showDatePicker -> TODO()
    }
  }
}