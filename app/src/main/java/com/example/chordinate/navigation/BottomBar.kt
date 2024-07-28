package com.example.chordinate.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController, items: List<BottomNavItem>) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        tonalElevation = 5.dp
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentScreen = navBackStackEntry.value?.destination?.route

        Row(modifier = Modifier.fillMaxWidth()) {
            items.forEach { item ->
                val isSelected = currentScreen == item.screen
                IconButton(
                    onClick = {
                        navController.navigate(item.screen) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.weight(1f).padding(8.dp)
                ) {
                    Column {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.name,
                            tint = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}