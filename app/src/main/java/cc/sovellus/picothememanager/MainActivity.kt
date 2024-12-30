package cc.sovellus.picothememanager

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import cc.sovellus.picothememanager.manager.EnvironmentManager
import cc.sovellus.picothememanager.ui.components.DisplayEnvironments
import cc.sovellus.picothememanager.ui.components.DisplayFeaturedEnvironments
import cc.sovellus.picothememanager.ui.theme.ThemetoolTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class MainActivity : ComponentActivity() {

    private lateinit var environmentManager: EnvironmentManager

    private var systemPackageStateFlow = MutableStateFlow(mutableStateListOf<PackageInfo>())
    private var systemPackages = systemPackageStateFlow.asStateFlow()

    private var customPackageStateFlow = MutableStateFlow(mutableStateListOf<PackageInfo>())
    private var customPackages = customPackageStateFlow.asStateFlow()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        environmentManager = EnvironmentManager(this)
        systemPackageStateFlow.value = environmentManager.getSystemPackageList()
        customPackageStateFlow.value = environmentManager.getPackageList()

        setContent {
            ThemetoolTheme {
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
                            colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color(0xff292929)),
                            actions = {
                                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.padding(16.dp).clickable(onClick = {
                                    systemPackageStateFlow.value = environmentManager.getSystemPackageList()
                                    customPackageStateFlow.value = environmentManager.getPackageList()

                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.toast_refresh_environments),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }), tint = Color.White)
                            }
                        )
                    },
                    containerColor = Color(0xff292929),
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            containerColor = Color(0xff424242),
                            contentColor = Color(0xffffffff),
                            onClick = {
                                if (ActivityCompat.checkSelfPermission(
                                        this, Constants.ANDROID_PERMISSION_SECURE_SETTINGS
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    environmentManager.resetEnvironment()
                                } else {
                                    Toast.makeText(
                                        this,
                                        this.getString(R.string.toast_no_permission),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            icon = { Icon(Icons.Filled.Clear, null) },
                            text = { Text(text = this.getString(R.string.button_reset_environment)) },
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

                        Text(
                            text = stringResource(R.string.label_more),
                            modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        DisplayEnvironments(systemPackages, environmentManager)

                        Text(
                            text = stringResource(R.string.label_custom),
                            modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        DisplayEnvironments(customPackages, environmentManager)
                    }
                }
            }
        }
    }
}