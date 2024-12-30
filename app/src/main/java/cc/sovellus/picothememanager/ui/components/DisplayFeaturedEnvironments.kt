package cc.sovellus.picothememanager.ui.components

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import cc.sovellus.picothememanager.Constants.PICO_SCENE_MANAGER
import cc.sovellus.picothememanager.R
import cc.sovellus.picothememanager.manager.EnvironmentManager
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("DiscouragedApi")
@Composable
fun DisplayFeaturedEnvironments(environmentManager: EnvironmentManager) {

    val context = LocalContext.current

    val lastClick = remember { mutableStateOf("") }
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

            items(thumbnails.size) {
                val name = thumbnails[it]

                if (name != "default_scene" && name != "AIGC" && name != "Emulator" && name != "Default")
                {
                    val assets = context.packageManager.getResourcesForApplication(PICO_SCENE_MANAGER).assets

                    val bitmap = remember { BitmapFactory.decodeStream(assets.open("thumbs/${name}/Scene_${name}_1_1.png")) }

                    val interactionSource = remember { MutableInteractionSource() }
                    val isHovered by interactionSource.collectIsHoveredAsState()

                    assets.close()

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
                                    if (lastClick.value != name) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.toast_scene_loading),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        environmentManager.applyEnvironment(PICO_SCENE_MANAGER, name)
                                        lastClick.value = name
                                    }
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            context.getString(R.string.toast_no_permission),
                                            Toast.LENGTH_LONG
                                        )
                                        .show()
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
                                text = name,
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
                }
            }
        }
    )
}