package com.openclassrooms.vitesse.presentation.screens.candidates.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.data.entity.Candidate
import com.openclassrooms.vitesse.presentation.screens.candidates.add.AddCandidateEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditCandidateViewModel(
  private val dao: CandidateDao,
  private val candidateId: Int
): ViewModel() {

  private val _state = MutableStateFlow(EditCandidateState())
  val state: StateFlow<EditCandidateState> = _state.asStateFlow()

  init {
    viewModelScope.launch {
      val candidate = dao.getCandidateById(candidateId)
      if (candidate != null) {
        _state.update {
          it.copy(
            id = candidate.id,
            firstName = candidate.firstName,
            lastName = candidate.lastName,
            phoneNumber = candidate.phoneNumber,
            email = candidate.email,
            birthDate = candidate.birthDate,
            expectedSalary = candidate.expectedSalary.toString(),
            notes = candidate.notes
          )
        }
      }
    }
  }

  fun onEvent(event: AddCandidateEvent) {
    when (event) {
      AddCandidateEvent.Cancel -> { /* no-op */ }
      AddCandidateEvent.SaveCandidate -> {
        val s = _state.value
        val candidate = Candidate(
          id = s.id,
          firstName = s.firstName,
          lastName = s.lastName,
          phoneNumber = s.phoneNumber,
          email = s.email,
          birthDate = s.birthDate ?: return,
          expectedSalary = s.expectedSalary.toIntOrNull() ?: 0,
          notes = s.notes
        )
        viewModelScope.launch {
          dao.upsertCandidate(candidate)
        }
      }
      is AddCandidateEvent.SetBirthDate -> _state.update { it.copy(birthDate = event.birthDate) }
      is AddCandidateEvent.SetEmail -> _state.update { it.copy(email = event.email) }
      is AddCandidateEvent.SetExpectedSalary -> _state.update { it.copy(expectedSalary = event.expectedSalary) }
      is AddCandidateEvent.SetFirstName -> _state.update { it.copy(firstName = event.firstName) }
      is AddCandidateEvent.SetLastName -> _state.update { it.copy(lastName = event.lastName) }
      is AddCandidateEvent.SetNotes -> _state.update { it.copy(notes = event.notes) }
      is AddCandidateEvent.SetPhoneNumber -> _state.update { it.copy(phoneNumber = event.phoneNumber) }
      AddCandidateEvent.hideDatePicker -> {}
      AddCandidateEvent.showDatePicker -> {}
    }
  }

  fun deleteCandidate() {
    val s = _state.value
    if (s.id == 0) return
    val candidate = Candidate(
      id = s.id,
      firstName = s.firstName,
      lastName = s.lastName,
      phoneNumber = s.phoneNumber,
      email = s.email,
      birthDate = s.birthDate ?: return,
      expectedSalary = s.expectedSalary.toIntOrNull() ?: 0,
      notes = s.notes
    )
    viewModelScope.launch {
      dao.deleteCandidate(candidate)
    }
  }
}
