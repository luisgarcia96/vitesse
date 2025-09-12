package com.openclassrooms.vitesse.presentation.screens.candidates.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.presentation.screens.candidates.add.AddCandidateContent
import com.openclassrooms.vitesse.presentation.screens.candidates.add.AddCandidateEvent

@Composable
fun EditCandidateScreen(
  dao: CandidateDao,
  candidateId: Int,
  onBack: () -> Unit
) {
  val viewModel = remember(candidateId) { EditCandidateViewModel(dao, candidateId) }
  val state by viewModel.state.collectAsState()

  AddCandidateContent(
    titleResId = R.string.edit_candidate,
    photoUri = state.photoUri,
    firstName = state.firstName,
    lastName = state.lastName,
    phoneNumber = state.phoneNumber,
    email = state.email,
    birthDate = state.birthDate,
    expectedSalary = state.expectedSalary,
    notes = state.notes,
    onBack = onBack,
    onPhotoSelected = { viewModel.onEvent(AddCandidateEvent.SetPhotoUri(it)) },
    onFirstNameChanged = { viewModel.onEvent(AddCandidateEvent.SetFirstName(it)) },
    onLastNameChanged = { viewModel.onEvent(AddCandidateEvent.SetLastName(it)) },
    onPhoneChanged = { viewModel.onEvent(AddCandidateEvent.SetPhoneNumber(it)) },
    onEmailChanged = { viewModel.onEvent(AddCandidateEvent.SetEmail(it)) },
    onDateSelected = { viewModel.onEvent(AddCandidateEvent.SetBirthDate(it)) },
    onExpectedSalaryChanged = { viewModel.onEvent(AddCandidateEvent.SetExpectedSalary(it)) },
    onNotesChanged = { viewModel.onEvent(AddCandidateEvent.SetNotes(it)) },
    onSave = { viewModel.onEvent(AddCandidateEvent.SaveCandidate) }
  )
}
