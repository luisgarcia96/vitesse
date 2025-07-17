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

  val context = LocalContext.current
  var showDatePicker by remember { mutableStateOf(false) }

  if (showDatePicker) {
    DatePickerDialog(
      context,
      { _, year, month, day ->
        viewModel.onEvent(AddCandidateEvent.SetBirthDate(LocalDate.of(year, month + 1, day)))
        showDatePicker = false
      },
      state.birthDate?.year ?: LocalDate.now().year,
      (state.birthDate?.monthValue?.minus(1)) ?: (LocalDate.now().monthValue - 1),
      state.birthDate?.dayOfMonth ?: LocalDate.now().dayOfMonth
    ).show()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Ajouter un candidat") },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
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
        value = state.firstName,
        onValueChange = { viewModel.onEvent(AddCandidateEvent.SetFirstName(it)) },
        label = { Text("Prénom") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = state.lastName,
        onValueChange = { viewModel.onEvent(AddCandidateEvent.SetLastName(it)) },
        label = { Text("Nom") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = state.phoneNumber,
        onValueChange = { viewModel.onEvent(AddCandidateEvent.SetPhoneNumber(it)) },
        label = { Text("Phone") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
      )

      OutlinedTextField(
        value = state.email,
        onValueChange = { viewModel.onEvent(AddCandidateEvent.SetEmail(it)) },
        label = { Text("Email") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
      )

      OutlinedTextField(
        value = state.birthDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
        onValueChange = {},
        label = { Text("Sélectionner une date") },
        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
        trailingIcon = {
          Icon(
            Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier.clickable { showDatePicker = true }
          )
        },
        readOnly = true,
        modifier = Modifier.fillMaxWidth()
      )

      Button(
        onClick = { viewModel.onEvent(AddCandidateEvent.SaveCandidate) },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Sauvegarder")
      }
    }
  }
}