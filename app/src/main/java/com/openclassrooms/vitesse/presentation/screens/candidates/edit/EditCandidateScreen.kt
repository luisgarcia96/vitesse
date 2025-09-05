package com.openclassrooms.vitesse.presentation.screens.candidates.edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
  var showConfirm by remember { mutableStateOf(false) }

  AddCandidateContent(
    titleResId = R.string.edit_candidate,
    firstName = state.firstName,
    lastName = state.lastName,
    phoneNumber = state.phoneNumber,
    email = state.email,
    birthDate = state.birthDate,
    expectedSalary = state.expectedSalary,
    notes = state.notes,
    onBack = onBack,
    onFirstNameChanged = { viewModel.onEvent(AddCandidateEvent.SetFirstName(it)) },
    onLastNameChanged = { viewModel.onEvent(AddCandidateEvent.SetLastName(it)) },
    onPhoneChanged = { viewModel.onEvent(AddCandidateEvent.SetPhoneNumber(it)) },
    onEmailChanged = { viewModel.onEvent(AddCandidateEvent.SetEmail(it)) },
    onDateSelected = { viewModel.onEvent(AddCandidateEvent.SetBirthDate(it)) },
    onExpectedSalaryChanged = { viewModel.onEvent(AddCandidateEvent.SetExpectedSalary(it)) },
    onNotesChanged = { viewModel.onEvent(AddCandidateEvent.SetNotes(it)) },
    onSave = { viewModel.onEvent(AddCandidateEvent.SaveCandidate) },
    bottomContent = {
      if (showConfirm) {
        AlertDialog(
          onDismissRequest = { showConfirm = false },
          title = { Text(stringResource(R.string.confirm_delete_title)) },
          text = { Text(stringResource(R.string.confirm_delete_message)) },
          confirmButton = {
            TextButton(onClick = {
              showConfirm = false
              viewModel.deleteCandidate()
              onBack()
            }) {
              Text(stringResource(R.string.confirm))
            }
          },
          dismissButton = {
            TextButton(onClick = { showConfirm = false }) {
              Text(stringResource(R.string.cancel))
            }
          }
        )
      }
      Spacer(Modifier.height(4.dp))
      Button(
        onClick = { showConfirm = true },
        modifier = Modifier.fillMaxWidth(),
        enabled = state.id != 0,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.error,
          contentColor = MaterialTheme.colorScheme.onError
        )
      ) {
        Text(stringResource(R.string.delete))
      }
    }
  )
}
