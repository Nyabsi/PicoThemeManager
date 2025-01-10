package cc.sovellus.picothememanager.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cc.sovellus.picothememanager.Constants.IGNORE_ENVIRONMENTS
import cc.sovellus.picothememanager.Constants.PICO_SCENE_MANAGER
import cc.sovellus.picothememanager.manager.EnvironmentManager

@SuppressLint("DiscouragedApi")
@Composable
fun DisplayFeaturedEnvironments(environmentManager: EnvironmentManager) {
    val context = LocalContext.current
    val thumbnails = remember { context.packageManager.getResourcesForApplication(PICO_SCENE_MANAGER).assets.list("thumbs")!!  }

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 4.dp,
            end = 16.dp,
            bottom = 4.dp
        ),
        modifier = Modifier.heightIn(max = 120.dp),
        content = {
            items(thumbnails) {
                if (!IGNORE_ENVIRONMENTS.contains(it)) {
                    ThemeComponent(environmentManager, PICO_SCENE_MANAGER, it, it)
                }
            }
        }
    )
}