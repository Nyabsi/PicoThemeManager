package cc.sovellus.picothememanager.ui.components

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cc.sovellus.picothememanager.manager.EnvironmentManager

@SuppressLint("DiscouragedApi")
@Composable
fun DisplayEnvironments(list: State<SnapshotStateList<PackageInfo>>, environmentManager: EnvironmentManager) {
    val context = LocalContext.current

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
            items(list.value) {
                val packageName = it.packageName
                val resources = context.packageManager.getResourcesForApplication(packageName)

                val sceneTagId = resources.getIdentifier("sceneTag", "string", packageName)
                val sceneTag = resources.getString(sceneTagId)

                val sceneNameId = resources.getIdentifier("SceneName_$sceneTag", "string", it.packageName)
                var sceneName = sceneTag
                if (sceneNameId > 0) {
                    sceneName = resources.getString(sceneNameId)
                }

                if (sceneTagId > 0) {
                    ThemeComponent(environmentManager, packageName, sceneTag, sceneName)
                }
            }
        }
    )
}