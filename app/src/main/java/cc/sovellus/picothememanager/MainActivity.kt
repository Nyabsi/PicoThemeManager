package cc.sovellus.picothememanager

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import cc.sovellus.picothememanager.manager.EnvironmentManager
import cc.sovellus.picothememanager.ui.theme.ThemetoolTheme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("DiscouragedApi")
@Composable
fun DisplayEnvironments(list: List<PackageInfo>, environmentManager: EnvironmentManager) {

    val context = LocalContext.current

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        modifier = Modifier.heightIn(max = 140.dp),
        content = {
            items(list) {
                val resources = context.packageManager.getResourcesForApplication(it.packageName)
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

                    val bitmap = remember { BitmapFactory.decodeStream(context.packageManager.getResourcesForApplication(it.packageName).assets.open("thumbs/${sceneTag}/Scene_${sceneTag}_1_1.png")) }

                    val interactionSource = remember { MutableInteractionSource() }
                    val isHovered by interactionSource.collectIsHoveredAsState()

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .height(120.dp)
                            .width(200.dp)
                            .clickable(onClick = {
                                if (ActivityCompat.checkSelfPermission(
                                        context,
                                        "android.permission.WRITE_SECURE_SETTINGS"
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    environmentManager.setEnvironment(
                                        it.packageName,
                                        sceneTag,
                                        "/assets/scene/$sceneTag/Scene_${sceneTag}_1_1.unity3d"
                                    )
                                    environmentManager.forceVrShellRestart()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "You need to grant android.permission.WRITE_SECURE_SETTINGS via ADB first!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                            .hoverable(interactionSource),
                        contentAlignment = Alignment.TopStart
                    ) {
                        GlideImage(
                            model = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .width(200.dp)
                                .zIndex(0f)
                                .clip(RoundedCornerShape(10)),
                            contentScale = ContentScale.Crop,
                            alpha = if (isHovered) { 0.4f } else { 1.0f },
                        )
                        if (isHovered) {
                            Text(
                                text = sceneName,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .zIndex(1f),
                                textAlign = TextAlign.Start,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White
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

class MainActivity : ComponentActivity() {

    private lateinit var environmentManager: EnvironmentManager

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        environmentManager = EnvironmentManager(this)

        setContent {
            ThemetoolTheme {
                val rememberScroll = rememberScrollState()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color(0xff292929)),
                        )
                    },
                    containerColor = Color(0xff292929),
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            containerColor = Color(0xff424242),
                            contentColor = Color(0xffffffff),
                            onClick = {
                                if (ActivityCompat.checkSelfPermission(
                                        this,
                                        "android.permission.WRITE_SECURE_SETTINGS"
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    environmentManager.resetEnvironment()
                                    environmentManager.forceVrShellRestart()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "You need to grant android.permission.WRITE_SECURE_SETTINGS via ADB first!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
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
                            .verticalScroll(rememberScroll)
                    ) {
                        Text(
                            text = "Official Themes",
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = Color.White
                        )

                        DisplayEnvironments(environmentManager.getPackageListOfficial(), environmentManager)

                        Text(
                            text = "Custom Themes",
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = Color.White
                        )

                        DisplayEnvironments(environmentManager.getPackageList(), environmentManager)
                    }
                }
            }
        }
    }
}