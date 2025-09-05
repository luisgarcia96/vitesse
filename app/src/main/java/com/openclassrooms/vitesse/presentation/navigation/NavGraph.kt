package com.openclassrooms.vitesse.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.presentation.screens.candidates.add.AddCandidateScreen
import com.openclassrooms.vitesse.presentation.screens.candidates.list.CandidatesListScreen
import com.openclassrooms.vitesse.presentation.screens.candidates.edit.EditCandidateScreen

object CandidateRoutes {
  const val LIST = "candidates_list"
  const val ADD  = "add_candidate"
  const val EDIT = "edit_candidate"
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
        },
        onOpenCandidate = { id ->
          navController.navigate("${CandidateRoutes.EDIT}/$id")
        }
      )
    }
    composable(CandidateRoutes.ADD) {
      AddCandidateScreen(
        dao = candidateDao,
        onBack = { navController.popBackStack() }
      )
    }
    composable(
      route = "${CandidateRoutes.EDIT}/{id}",
      arguments = listOf(navArgument("id") { type = NavType.IntType })
    ) { backStackEntry ->
      val id = backStackEntry.arguments?.getInt("id") ?: return@composable
      EditCandidateScreen(
        dao = candidateDao,
        candidateId = id,
        onBack = { navController.popBackStack() }
      )
    }
  }
}
