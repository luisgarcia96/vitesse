package com.openclassrooms.vitesse.presentation.screens.candidates.add

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.openclassrooms.vitesse.data.dao.CandidateDao
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.Period
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCandidateScreen(
  dao: CandidateDao,
  onBack: () -> Unit
) {
  // **SIMPLER VM INJECTION**:
  val viewModel = remember { AddCandidateViewModel(dao) }

  // collect state directly
  val state by viewModel.state.collectAsState()

  AddCandidateContent(
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
    onSave = { viewModel.onEvent(AddCandidateEvent.SaveCandidate) }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCandidateContent(
  firstName: String,
  lastName: String,
  phoneNumber: String,
  email: String,
  birthDate: LocalDate?,
  expectedSalary: String,
  notes: String,
  onBack: () -> Unit,
  onFirstNameChanged: (String) -> Unit,
  onLastNameChanged: (String) -> Unit,
  onPhoneChanged: (String) -> Unit,
  onEmailChanged: (String) -> Unit,
  onDateSelected: (LocalDate) -> Unit,
  onExpectedSalaryChanged: (String) -> Unit,
  onNotesChanged: (String) -> Unit,
  onSave: () -> Unit
) {
  val context = LocalContext.current
  var showDatePicker by remember { mutableStateOf(false) }

  val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

  var dateText by remember { mutableStateOf(birthDate?.format(dateFormatter) ?: "") }
  var dateError by remember { mutableStateOf(false) }

  if (showDatePicker) {
    DatePickerDialog(
      context,
      { _, year, month, day ->
        val selected = LocalDate.of(year, month + 1, day)
        val age = Period.between(selected, LocalDate.now()).years
        if (age < 18) {
          dateError = true
        } else {
          onDateSelected(selected)
          dateText = selected.format(dateFormatter)
          dateError = false
        }
        showDatePicker = false
      },
      birthDate?.year ?: LocalDate.now().year,
      (birthDate?.monthValue?.minus(1)) ?: (LocalDate.now().monthValue - 1),
      birthDate?.dayOfMonth ?: LocalDate.now().dayOfMonth
    ).show()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Ajouter un candidat") },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
          }
        }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
      ) {
        // TODO: image placeholder
      }

      OutlinedTextField(
        value = firstName,
        onValueChange = onFirstNameChanged,
        label = { Text("Prénom") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = lastName,
        onValueChange = onLastNameChanged,
        label = { Text("Nom") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneChanged,
        label = { Text("Phone") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
      )

      OutlinedTextField(
        value = email,
        onValueChange = onEmailChanged,
        label = { Text("Email") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
      )

      OutlinedTextField(
        value = dateText,
        onValueChange = {
          dateText = it
          if (it.isBlank()) {
            dateError = false
          } else {
            try {
              val parsed = LocalDate.parse(it, dateFormatter)
              val age = Period.between(parsed, LocalDate.now()).years
              if (age < 18) {
                dateError = true
              } else {
                dateError = false
                onDateSelected(parsed)
              }
            } catch (_: Exception) {
              dateError = true
            }
          }
        },
        label = { Text("Date de naissance") },
        placeholder = { Text("jj/MM/aaaa") },
        isError = dateError,
        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
        trailingIcon = {
          Icon(
            Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier.clickable { showDatePicker = true }
          )
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        supportingText = {
          if (dateError) Text("Date invalide ou candidat doit avoir au moins 18 ans (jj/MM/aaaa)")
        }
      )

      OutlinedTextField(
        value = expectedSalary,
        onValueChange = onExpectedSalaryChanged,
        label = { Text("Salaire attendu") },
        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
      )

      OutlinedTextField(
        value = notes,
        onValueChange = onNotesChanged,
        label = { Text("Notes") },
        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) },
        modifier = Modifier
          .fillMaxWidth()
          .height(120.dp),
        maxLines = 5
      )

      Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Sauvegarder")
      }
    }
  }
}

@Preview(showBackground = true, name = "Add Candidate – Empty")
@Composable
private fun AddCandidate_Empty_Preview() {
  AddCandidateContent(
    firstName = "",
    lastName = "",
    phoneNumber = "",
    email = "",
    birthDate = null,
    expectedSalary = "",
    notes = "",
    onBack = {},
    onFirstNameChanged = {},
    onLastNameChanged = {},
    onPhoneChanged = {},
    onEmailChanged = {},
    onDateSelected = {},
    onExpectedSalaryChanged = {},
    onNotesChanged = {},
    onSave = {}
  )
}

@Preview(showBackground = true, name = "Add Candidate – Filled")
@Composable
private fun AddCandidate_Filled_Preview() {
  AddCandidateContent(
    firstName = "Ada",
    lastName = "Lovelace",
    phoneNumber = "+33 6 12 34 56 78",
    email = "ada@example.com",
    birthDate = LocalDate.of(1815, 12, 10),
    expectedSalary = "85000",
    notes = "Pioneer in computing",
    onBack = {},
    onFirstNameChanged = {},
    onLastNameChanged = {},
    onPhoneChanged = {},
    onEmailChanged = {},
    onDateSelected = {},
    onExpectedSalaryChanged = {},
    onNotesChanged = {},
    onSave = {}
  )
}