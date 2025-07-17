package com.openclassrooms.vitesse.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.presentation.screens.candidates.add.AddCandidateScreen
import com.openclassrooms.vitesse.presentation.screens.candidates.list.CandidatesListScreen

object CandidateRoutes {
  const val LIST = "candidates_list"
  const val ADD  = "add_candidate"
}

@Composable
fun AppNavGraph(
  candidateDao: CandidateDao,
  startDestination: String = CandidateRoutes.LIST
) {
  val navController = rememberNavController()

  NavHost(navController = navController, startDestination = startDestination) {
    composable(CandidateRoutes.LIST) {
      CandidatesListScreen(
        dao = candidateDao,
        onAddCandidate = {
          navController.navigate(CandidateRoutes.ADD)
        }
      )
    }
    composable(CandidateRoutes.ADD) {
      AddCandidateScreen(
        dao = candidateDao,
        onBack = { navController.popBackStack() }
      )
    }
  }
}