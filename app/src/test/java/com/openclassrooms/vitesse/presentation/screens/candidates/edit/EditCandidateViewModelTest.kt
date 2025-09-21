package com.openclassrooms.vitesse.presentation.screens.candidates.edit

import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.data.entity.Candidate
import com.openclassrooms.vitesse.presentation.screens.candidates.add.AddCandidateEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class EditCandidateViewModelTest {

  private val dispatcher = StandardTestDispatcher()
  private lateinit var dao: RecordingCandidateDao

  @Before
  fun setUp() {
    Dispatchers.setMain(dispatcher)
    dao = RecordingCandidateDao()
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `init loads candidate into state`() = runTest(dispatcher) {
    val candidate = candidate(id = 42, firstName = "Ada")
    dao.candidates[42] = candidate

    val viewModel = EditCandidateViewModel(dao, candidateId = 42)
    dispatcher.scheduler.advanceUntilIdle()

    val state = viewModel.state.value
    assertEquals(candidate.id, state.id)
    assertEquals(candidate.firstName, state.firstName)
    assertEquals(candidate.lastName, state.lastName)
    assertEquals(candidate.expectedSalary.toString(), state.expectedSalary)
    assertEquals(candidate.birthDate, state.birthDate)
    assertEquals(candidate.notes, state.notes)
    assertEquals(candidate.photoUri, state.photoUri)
    assertTrue(state.isFavorite)
  }

  @Test
  fun `save candidate persists updated data`() = runTest(dispatcher) {
    val original = candidate(id = 7)
    dao.candidates[7] = original

    val viewModel = EditCandidateViewModel(dao, candidateId = 7)
    dispatcher.scheduler.advanceUntilIdle()

    viewModel.onEvent(AddCandidateEvent.SetFirstName("Grace"))
    viewModel.onEvent(AddCandidateEvent.SetLastName("Hopper"))
    viewModel.onEvent(AddCandidateEvent.SetPhoneNumber("555-0101"))
    viewModel.onEvent(AddCandidateEvent.SetEmail("grace@example.com"))
    viewModel.onEvent(AddCandidateEvent.SetNotes("COBOL legend"))
    viewModel.onEvent(AddCandidateEvent.SetExpectedSalary("120000"))
    viewModel.onEvent(AddCandidateEvent.SetBirthDate(LocalDate.of(1906, 12, 9)))
    viewModel.onEvent(AddCandidateEvent.SetPhotoUri("photo://updated"))

    viewModel.onEvent(AddCandidateEvent.SaveCandidate)
    dispatcher.scheduler.advanceUntilIdle()

    val saved = dao.upserts.single()
    assertEquals(7, saved.id)
    assertEquals("Grace", saved.firstName)
    assertEquals("Hopper", saved.lastName)
    assertEquals("555-0101", saved.phoneNumber)
    assertEquals("grace@example.com", saved.email)
    assertEquals(LocalDate.of(1906, 12, 9), saved.birthDate)
    assertEquals(120000, saved.expectedSalary)
    assertEquals("COBOL legend", saved.notes)
    assertEquals("photo://updated", saved.photoUri)
  }

  @Test
  fun `save candidate does nothing when birth date missing`() = runTest(dispatcher) {
    val viewModel = EditCandidateViewModel(dao, candidateId = 0)
    dispatcher.scheduler.advanceUntilIdle()

    viewModel.onEvent(AddCandidateEvent.SetFirstName("Ada"))
    viewModel.onEvent(AddCandidateEvent.SetLastName("Lovelace"))
    viewModel.onEvent(AddCandidateEvent.SetPhoneNumber("01234"))
    viewModel.onEvent(AddCandidateEvent.SetEmail("ada@example.com"))
    viewModel.onEvent(AddCandidateEvent.SetNotes("notes"))
    viewModel.onEvent(AddCandidateEvent.SetExpectedSalary("1000"))

    viewModel.onEvent(AddCandidateEvent.SaveCandidate)
    dispatcher.scheduler.advanceUntilIdle()

    assertTrue(dao.upserts.isEmpty())
  }

  @Test
  fun `delete candidate ignores empty id`() = runTest(dispatcher) {
    val viewModel = EditCandidateViewModel(dao, candidateId = 0)
    dispatcher.scheduler.advanceUntilIdle()

    viewModel.deleteCandidate()
    dispatcher.scheduler.advanceUntilIdle()

    assertTrue(dao.deletes.isEmpty())
  }

  @Test
  fun `delete candidate removes existing candidate`() = runTest(dispatcher) {
    val candidate = candidate(id = 11)
    dao.candidates[11] = candidate

    val viewModel = EditCandidateViewModel(dao, candidateId = 11)
    dispatcher.scheduler.advanceUntilIdle()

    viewModel.deleteCandidate()
    dispatcher.scheduler.advanceUntilIdle()

    assertEquals(1, dao.deletes.size)
    val deleted = dao.deletes.first()
    assertEquals(11, deleted.id)
    assertEquals(candidate.firstName, deleted.firstName)
    assertEquals(candidate.email, deleted.email)
  }

  @Test
  fun `set favorite updates state and dao`() = runTest(dispatcher) {
    val candidate = candidate(id = 5, isFavorite = false)
    dao.candidates[5] = candidate

    val viewModel = EditCandidateViewModel(dao, candidateId = 5)
    dispatcher.scheduler.advanceUntilIdle()

    viewModel.setFavorite(true)
    dispatcher.scheduler.advanceUntilIdle()

    assertEquals(listOf(5 to true), dao.favoriteUpdates)
    assertTrue(viewModel.state.value.isFavorite)
  }

  private fun candidate(
    id: Int,
    firstName: String = "First$id",
    lastName: String = "Last$id",
    phone: String = "555$id",
    email: String = "user$id@example.com",
    birthDate: LocalDate = LocalDate.of(2000, 1, 1),
    expectedSalary: Int = 42_000,
    notes: String = "Notes$id",
    photoUri: String? = "photo://$id",
    isFavorite: Boolean = true
  ) = Candidate(
    id = id,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phone,
    email = email,
    birthDate = birthDate,
    expectedSalary = expectedSalary,
    notes = notes,
    photoUri = photoUri,
    isFavorite = isFavorite
  )

  private class RecordingCandidateDao : CandidateDao {
    val candidates = mutableMapOf<Int, Candidate>()
    val upserts = mutableListOf<Candidate>()
    val deletes = mutableListOf<Candidate>()
    val favoriteUpdates = mutableListOf<Pair<Int, Boolean>>()

    override suspend fun upsertCandidate(candidate: Candidate) {
      upserts.add(candidate)
      candidates[candidate.id] = candidate
    }

    override fun getAllCandidates(): Flow<List<Candidate>> =
      throw NotImplementedError("Not required for tests")

    override fun getFavoriteCandidates(): Flow<List<Candidate>> =
      throw NotImplementedError("Not required for tests")

    override suspend fun getCandidateById(id: Int): Candidate? = candidates[id]

    override suspend fun setFavorite(id: Int, isFavorite: Boolean) {
      favoriteUpdates.add(id to isFavorite)
      candidates[id]?.let { candidates[id] = it.copy(isFavorite = isFavorite) }
    }

    override suspend fun deleteCandidate(candidate: Candidate) {
      deletes.add(candidate)
      candidates.remove(candidate.id)
    }
  }
}
