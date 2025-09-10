package com.openclassrooms.vitesse.presentation.screens.candidates.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.FilledIconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.presentation.screens.candidates.edit.EditCandidateViewModel
import java.time.format.DateTimeFormatter
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate
import java.time.Period
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.core.net.toUri

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
      val context = LocalContext.current

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          FilledIconButton(
            onClick = {
              if (phoneNumber.isNotBlank()) {
                val intent = Intent(Intent.ACTION_DIAL, ("tel:" + phoneNumber).toUri())
                context.startActivity(intent)
              }
            },
            enabled = phoneNumber.isNotBlank(),
            modifier = Modifier.size(56.dp)
          ) {
            Icon(Icons.Filled.Call, contentDescription = stringResource(R.string.call))
          }
          Text(text = stringResource(R.string.call), style = MaterialTheme.typography.labelMedium)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          FilledIconButton(
            onClick = {
              if (phoneNumber.isNotBlank()) {
                val intent = Intent(Intent.ACTION_SENDTO, ("smsto:" + phoneNumber).toUri())
                context.startActivity(intent)
              }
            },
            enabled = phoneNumber.isNotBlank(),
            modifier = Modifier.size(56.dp)
          ) {
            Icon(Icons.Filled.Sms, contentDescription = stringResource(R.string.sms))
          }
          Text(text = stringResource(R.string.sms), style = MaterialTheme.typography.labelMedium)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          FilledIconButton(
            onClick = {
              if (email.isNotBlank()) {
                val intent = Intent(Intent.ACTION_SENDTO, ("mailto:" + email).toUri())
                context.startActivity(intent)
              }
            },
            enabled = email.isNotBlank(),
            modifier = Modifier.size(56.dp)
          ) {
            Icon(Icons.Filled.Email, contentDescription = stringResource(R.string.email))
          }
          Text(text = stringResource(R.string.email), style = MaterialTheme.typography.labelMedium)
        }
      }

      // About card
      Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = stringResource(R.string.about), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = stringResource(R.string.birth_date), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(
            text = birthDate?.let {
              val years = Period.between(it, LocalDate.now()).years
              "${it.format(dateFormatter)} (${years} ${stringResource(R.string.years)})"
            } ?: ""
          )
        }
      }

      // Expected Salary card
      Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = stringResource(R.string.expected_salary), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = expectedSalary)
        }
      }

      // Notes card
      if (notes.isNotBlank()) {
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResource(R.string.notes), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
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
