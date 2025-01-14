package cc.sovellus.picothememanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import cc.sovellus.picothememanager.Constants.NOTIFICATION_CHANNEL_DEFAULT
import cc.sovellus.picothememanager.manager.EnvironmentManager
import cc.sovellus.picothememanager.ui.components.DisplayEnvironments
import cc.sovellus.picothememanager.ui.components.DisplayFeaturedEnvironments
import cc.sovellus.picothememanager.ui.components.ResolutionDropdown
import cc.sovellus.picothememanager.ui.theme.ThemetoolTheme
import cc.sovellus.picothememanager.utils.checkSecurePermission
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    private lateinit var environmentManager: EnvironmentManager
    private var systemPackageStateFlow = MutableStateFlow(mutableStateListOf<PackageInfo>())
    private var customPackageStateFlow = MutableStateFlow(mutableStateListOf<PackageInfo>())

    private fun updateThemes() {
        systemPackageStateFlow.value = environmentManager.getSystemPackageList()
        customPackageStateFlow.value = environmentManager.getPackageList()
        environmentManager.clearThumbnailCache()
    }

    override fun onResume() {
        super.onResume()
        updateThemes()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        environmentManager = EnvironmentManager(this)
        environmentManager.playEnvironmentAudio()

        updateThemes()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),
                    0
                )
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    0
                )
            }
        }

        val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val defaultChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_DEFAULT,
            "Default",
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(defaultChannel)

        setContent {
            ThemetoolTheme {
                val systemPackages = systemPackageStateFlow.collectAsState()
                val customPackages = customPackageStateFlow.collectAsState()
                val context = LocalContext.current

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.White
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                            actions = {
                                val menuExpanded = remember { mutableStateOf(false) }
                                ResolutionDropdown(menuExpanded, environmentManager)

                                IconButton(onClick = {
                                    if (!environmentManager.isResolutionOptionAvailable()) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.resolution_change_unavailable),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@IconButton
                                    }

                                    context.checkSecurePermission {
                                        menuExpanded.value = true
                                    }
                                }) {
                                    Icon(
                                        Icons.Filled.Build,
                                        contentDescription = null,
                                        tint = if (environmentManager.isResolutionOptionAvailable()) {
                                            Color.White
                                        } else {
                                            Color.Gray
                                        }
                                    )
                                }

                                IconButton(onClick = {
                                    updateThemes()

                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.toast_refresh_environments),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }) {
                                    Icon(Icons.Filled.Refresh, contentDescription = null, tint = Color.White)
                                }
                            }
                        )
                    },
                    containerColor = Color.Transparent,
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            containerColor = Color(0xff424242),
                            contentColor = Color(0xffffffff),
                            onClick = {
                                context.checkSecurePermission {
                                    environmentManager.resetEnvironment()
                                }
                            },
                            icon = { Icon(Icons.Filled.Clear, null) },
                            text = { Text(text = this.getString(R.string.button_reset_environment)) },
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0xff292929), shape = RoundedCornerShape(32.dp))) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(
                                top = innerPadding.calculateTopPadding(),
                                bottom = innerPadding.calculateBottomPadding()
                            )
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {

                        Text(
                            text = stringResource(R.string.label_featured),
                            modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        DisplayFeaturedEnvironments(environmentManager)

                        if (!systemPackages.value.isEmpty()) {
                            Text(
                                text = stringResource(R.string.label_more),
                                modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }

                        DisplayEnvironments(systemPackages, environmentManager)

                        if (!customPackages.value.isEmpty()) {
                            Text(
                                text = stringResource(R.string.label_custom),
                                modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }

                        DisplayEnvironments(customPackages, environmentManager)
                    }
                }
            }
        }
    }
}