package com.openclassrooms.vitesse.presentation.screens.candidates.add

import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.data.entity.Candidate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AddCandidateViewModelTest {

  private val dispatcher = StandardTestDispatcher()
  private lateinit var dao: FakeCandidateDao
  private lateinit var viewModel: AddCandidateViewModel

  @Before
  fun setUp() {
    Dispatchers.setMain(dispatcher)
    dao = FakeCandidateDao()
    viewModel = AddCandidateViewModel(dao)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `field setters update state`() {
    val birthDate = LocalDate.of(1995, 5, 21)

    viewModel.onEvent(AddCandidateEvent.SetFirstName("Ada"))
    viewModel.onEvent(AddCandidateEvent.SetLastName("Lovelace"))
    viewModel.onEvent(AddCandidateEvent.SetPhoneNumber("0123456789"))
    viewModel.onEvent(AddCandidateEvent.SetEmail("ada@example.com"))
    viewModel.onEvent(AddCandidateEvent.SetExpectedSalary("75000"))
    viewModel.onEvent(AddCandidateEvent.SetNotes("Notes"))
    viewModel.onEvent(AddCandidateEvent.SetBirthDate(birthDate))
    viewModel.onEvent(AddCandidateEvent.SetPhotoUri("photo://uri"))
    viewModel.onEvent(AddCandidateEvent.hideDatePicker)

    val state = viewModel.state.value
    assertEquals("Ada", state.firstName)
    assertEquals("Lovelace", state.lastName)
    assertEquals("0123456789", state.phoneNumber)
    assertEquals("ada@example.com", state.email)
    assertEquals("75000", state.expectedSalary)
    assertEquals("Notes", state.notes)
    assertEquals(birthDate, state.birthDate)
    assertEquals("photo://uri", state.photoUri)
  }

  @Test
  fun `save candidate with complete data persists and resets state`() {
    val birthDate = LocalDate.of(1990, 1, 15)
    populateValidFields(birthDate = birthDate, expectedSalary = "80000")

    viewModel.onEvent(AddCandidateEvent.SaveCandidate)
    dispatcher.scheduler.advanceUntilIdle()

    assertEquals(1, dao.saved.size)
    val savedCandidate = dao.saved.first()
    assertEquals("Ada", savedCandidate.firstName)
    assertEquals("Lovelace", savedCandidate.lastName)
    assertEquals("0123456789", savedCandidate.phoneNumber)
    assertEquals("ada@example.com", savedCandidate.email)
    assertEquals(birthDate, savedCandidate.birthDate)
    assertEquals(80000, savedCandidate.expectedSalary)
    assertEquals("Notes", savedCandidate.notes)
    assertEquals("photo://uri", savedCandidate.photoUri)
    assertTrue(savedCandidate.isFavorite.not())

    val resetState = viewModel.state.value
    assertEquals(AddCandidateState(), resetState)
  }

  @Test
  fun `save candidate with missing fields does nothing`() {
    viewModel.onEvent(AddCandidateEvent.SaveCandidate)
    dispatcher.scheduler.advanceUntilIdle()

    assertTrue(dao.saved.isEmpty())
    assertEquals(AddCandidateState(), viewModel.state.value)
  }

  @Test
  fun `invalid salary defaults to zero`() {
    populateValidFields(expectedSalary = "not-a-number")

    viewModel.onEvent(AddCandidateEvent.SaveCandidate)
    dispatcher.scheduler.advanceUntilIdle()

    val savedCandidate = dao.saved.single()
    assertEquals(0, savedCandidate.expectedSalary)
  }

  @Test
  fun `missing last name prevents saving`() {
    populateValidFields()
    viewModel.onEvent(AddCandidateEvent.SetLastName(""))

    viewModel.onEvent(AddCandidateEvent.SaveCandidate)
    dispatcher.scheduler.advanceUntilIdle()

    assertTrue(dao.saved.isEmpty())
  }

  @Test
  fun `missing phone prevents saving`() {
    populateValidFields()
    viewModel.onEvent(AddCandidateEvent.SetPhoneNumber(""))

    viewModel.onEvent(AddCandidateEvent.SaveCandidate)
    dispatcher.scheduler.advanceUntilIdle()

    assertTrue(dao.saved.isEmpty())
  }

  @Test
  fun `missing email prevents saving`() {
    populateValidFields()
    viewModel.onEvent(AddCandidateEvent.SetEmail(""))

    viewModel.onEvent(AddCandidateEvent.SaveCandidate)
    dispatcher.scheduler.advanceUntilIdle()

    assertTrue(dao.saved.isEmpty())
  }

  @Test
  fun `missing birth date prevents saving`() {
    viewModel.onEvent(AddCandidateEvent.SetFirstName("Ada"))
    viewModel.onEvent(AddCandidateEvent.SetLastName("Lovelace"))
    viewModel.onEvent(AddCandidateEvent.SetPhoneNumber("0123456789"))
    viewModel.onEvent(AddCandidateEvent.SetEmail("ada@example.com"))
    viewModel.onEvent(AddCandidateEvent.SetExpectedSalary("75000"))
    viewModel.onEvent(AddCandidateEvent.SetNotes("Notes"))
    viewModel.onEvent(AddCandidateEvent.SetPhotoUri("photo://uri"))

    viewModel.onEvent(AddCandidateEvent.SaveCandidate)
    dispatcher.scheduler.advanceUntilIdle()

    assertTrue(dao.saved.isEmpty())
  }

  @Test
  fun `cancel event is not implemented`() {
    assertThrows(NotImplementedError::class.java) {
      viewModel.onEvent(AddCandidateEvent.Cancel)
    }
  }

  @Test
  fun `show date picker event is not implemented`() {
    assertThrows(NotImplementedError::class.java) {
      viewModel.onEvent(AddCandidateEvent.showDatePicker)
    }
  }

  private fun populateValidFields(
    birthDate: LocalDate = LocalDate.of(1995, 5, 21),
    expectedSalary: String = "75000"
  ) {
    viewModel.onEvent(AddCandidateEvent.SetFirstName("Ada"))
    viewModel.onEvent(AddCandidateEvent.SetLastName("Lovelace"))
    viewModel.onEvent(AddCandidateEvent.SetPhoneNumber("0123456789"))
    viewModel.onEvent(AddCandidateEvent.SetEmail("ada@example.com"))
    viewModel.onEvent(AddCandidateEvent.SetNotes("Notes"))
    viewModel.onEvent(AddCandidateEvent.SetBirthDate(birthDate))
    viewModel.onEvent(AddCandidateEvent.SetExpectedSalary(expectedSalary))
    viewModel.onEvent(AddCandidateEvent.SetPhotoUri("photo://uri"))
  }

  private class FakeCandidateDao : CandidateDao {
    val saved = mutableListOf<Candidate>()

    override suspend fun upsertCandidate(candidate: Candidate) {
      saved.add(candidate)
    }

    override fun getAllCandidates(): Flow<List<Candidate>> =
      throw NotImplementedError()

    override fun getFavoriteCandidates(): Flow<List<Candidate>> =
      throw NotImplementedError()

    override suspend fun getCandidateById(id: Int): Candidate? =
      throw NotImplementedError()

    override suspend fun setFavorite(id: Int, isFavorite: Boolean) {
      throw NotImplementedError()
    }

    override suspend fun deleteCandidate(candidate: Candidate) {
      throw NotImplementedError()
    }
  }
}
