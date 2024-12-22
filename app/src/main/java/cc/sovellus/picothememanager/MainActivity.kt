package cc.sovellus.picothememanager

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.pm.PackageInfo
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.sovellus.picothememanager.ui.theme.ThemetoolTheme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage


class MainActivity : ComponentActivity() {

    private lateinit var context: Context

    private fun restartShell() {
        val intent = Intent()
        intent.component = ComponentName("com.pvr.vrshell", "com.pvr.vrshell.MainActivity")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun getPackageList(): List<PackageInfo> {
        return packageManager.getInstalledPackages(0).filter {
            it.packageName.contains(Regex("com.pvr.[^.]+.scene"))
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        context = this

        setContent {
            ThemetoolTheme {
                Scaffold(
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            onClick = {
                                Settings.Global.putString(
                                    context.contentResolver,
                                    "SceneManager.CurPackage",
                                    null
                                )
                                Settings.Global.putString(
                                    context.contentResolver,
                                    "SceneManager.CurrentScene",
                                    ""
                                )
                                Settings.Global.putString(
                                    context.contentResolver,
                                    "current_scene",
                                    "default_scene"
                                )
                                restartShell()
                            },
                            icon = { Icon(Icons.Filled.Refresh, null) },
                            text = { Text(text = "Reset Environment") },
                        )
                    },
                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(
                                top = innerPadding.calculateTopPadding(),
                                bottom = innerPadding.calculateBottomPadding()
                            )
                            .fillMaxSize()
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            contentPadding = PaddingValues(
                                start = 12.dp,
                                top = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            content = {
                                items(getPackageList()) {
                                    val resources = packageManager.getResourcesForApplication(it.packageName)
                                    val sceneTagId = resources.getIdentifier("sceneTag", "string", it.packageName)

                                    if (sceneTagId > 0) {

                                        val sceneTag = resources.getString(sceneTagId)
                                        val sceneNameId = resources.getIdentifier("SceneName_$sceneTag", "string", it.packageName)

                                        var sceneName = sceneTag
                                        if (sceneNameId > 0) {
                                           sceneName = resources.getString(sceneNameId)
                                        } else {
                                            Log.e("PicoThemeManager", "sceneNameId was 0 for ${it.packageName}")
                                        }

                                        val asset = context.packageManager.getResourcesForApplication(it.packageName).assets.open("thumbs/${sceneTag}/Scene_${sceneTag}_1_1.png")

                                        ElevatedCard(
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = 2.dp
                                            ),
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .fillMaxWidth()
                                                .height(180.dp)
                                                .width(200.dp)
                                                .clickable(onClick = {
                                                    Settings.Global.putString(
                                                        context.contentResolver,
                                                        "SceneManager.CurPackage",
                                                        it.packageName
                                                    )
                                                    Settings.Global.putString(
                                                        context.contentResolver,
                                                        "SceneManager.CurrentScene",
                                                        sceneTag
                                                    )
                                                    Settings.Global.putString(
                                                        context.contentResolver,
                                                        "current_scene",
                                                        "/assets/scene/$sceneTag/Scene_${sceneTag}_1_1.unity3d"
                                                    )
                                                    restartShell()
                                                })
                                        ) {

                                            GlideImage(
                                                model = BitmapFactory.decodeStream(asset),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .width(200.dp),
                                                contentScale = ContentScale.Crop
                                            )

                                            Row(
                                                modifier = Modifier.padding(4.dp)
                                            ) {
                                                Text(
                                                    text = sceneName,
                                                    textAlign = TextAlign.Start,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    } else {
                                        Log.e("PicoThemeManager", "sceneTagId was 0 for ${it.packageName}")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}