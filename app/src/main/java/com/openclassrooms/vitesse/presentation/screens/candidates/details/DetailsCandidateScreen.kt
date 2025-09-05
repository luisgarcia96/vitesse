package com.openclassrooms.vitesse.presentation.screens.candidates.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.presentation.screens.candidates.edit.EditCandidateViewModel
import java.time.format.DateTimeFormatter
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsCandidateScreen(
  dao: CandidateDao,
  candidateId: Int,
  onBack: () -> Unit,
  onEdit: (Int) -> Unit
) {
  val viewModel = remember(candidateId) { EditCandidateViewModel(dao, candidateId) }
  val state by viewModel.state.collectAsState()

  DetailsCandidateContent(
    id = state.id,
    firstName = state.firstName,
    lastName = state.lastName,
    phoneNumber = state.phoneNumber,
    email = state.email,
    birthDate = state.birthDate,
    expectedSalary = state.expectedSalary,
    notes = state.notes,
    isFavorite = state.isFavorite,
    onBack = onBack,
    onEdit = onEdit,
    onToggleFavorite = { fav -> viewModel.setFavorite(fav) },
    onDeleteConfirmed = {
      viewModel.deleteCandidate()
      onBack()
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsCandidateContent(
  id: Int,
  firstName: String,
  lastName: String,
  phoneNumber: String,
  email: String,
  birthDate: LocalDate?,
  expectedSalary: String,
  notes: String,
  isFavorite: Boolean,
  onBack: () -> Unit,
  onEdit: (Int) -> Unit,
  onToggleFavorite: (Boolean) -> Unit,
  onDeleteConfirmed: () -> Unit,
) {
  var showConfirm by remember { mutableStateOf(false) }
  val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("$firstName $lastName".trim(), maxLines = 1) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
        },
        actions = {
          IconButton(onClick = { onToggleFavorite(!isFavorite) }) {
            val icon = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline
            Icon(icon, contentDescription = null)
          }
          IconButton(onClick = { onEdit(id) }) {
            Icon(Icons.Filled.Edit, contentDescription = null)
          }
          IconButton(onClick = { showConfirm = true }) {
            Icon(Icons.Filled.Delete, contentDescription = null)
          }
        }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = stringResource(R.string.first_name), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = firstName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
          Text(text = stringResource(R.string.last_name), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = lastName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
        }
      }

      Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = stringResource(R.string.phone_number), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = phoneNumber)
          Text(text = stringResource(R.string.email), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = email)
        }
      }

      Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = stringResource(R.string.birth_date), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = birthDate?.format(dateFormatter) ?: "")
          Text(text = stringResource(R.string.expected_salary), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = expectedSalary)
        }
      }

      if (notes.isNotBlank()) {
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResource(R.string.notes), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(text = notes)
          }
        }
      }
    }
  }

  if (showConfirm) {
    AlertDialog(
      onDismissRequest = { showConfirm = false },
      title = { Text(stringResource(R.string.confirm_delete_title)) },
      text = { Text(stringResource(R.string.confirm_delete_message)) },
      confirmButton = {
        TextButton(onClick = {
          showConfirm = false
          onDeleteConfirmed()
        }) { Text(stringResource(R.string.confirm)) }
      },
      dismissButton = {
        TextButton(onClick = { showConfirm = false }) { Text(stringResource(R.string.cancel)) }
      }
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun DetailsCandidateScreen_Preview() {
  DetailsCandidateContent(
    id = 1,
    firstName = "Ada",
    lastName = "Lovelace",
    phoneNumber = "06 12 34 56 78",
    email = "ada@example.com",
    birthDate = LocalDate.of(1815, 12, 10),
    expectedSalary = "€55,000",
    notes = "Excels at algorithms. Available from October.",
    isFavorite = false,
    onBack = {},
    onEdit = {},
    onToggleFavorite = {},
    onDeleteConfirmed = {}
  )
}
