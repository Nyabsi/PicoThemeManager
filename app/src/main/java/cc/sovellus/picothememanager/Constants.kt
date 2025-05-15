// PicoThemeManager
// Copyright (C) 2025 Nyabsi <nyabsi@sovellus.cc>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.


package cc.sovellus.picothememanager

object Constants {
    const val ANDROID_PERMISSION_SECURE_SETTINGS = "android.permission.WRITE_SECURE_SETTINGS"
    const val PICO_SCENE_MANAGER = "com.pvr.scenemanager"
    const val PICO_VRSHELL = "com.pvr.vrshell"
    const val PICO_PERMISSION_CONTROLLER = "com.android.permissioncontroller"
    const val APP_PERMISSIONS_ACTIVITY_SPARROW = "com.android.permissioncontroller.permission.ui.pico.AppPermissionsActivity"
    const val APP_PERMISSIONS_ACTIVITY_LEGACY = "com.android.packageinstaller.permission.ui.pico.AppPermissionsActivity"
    const val VRSHELL_MINIMUM_VERSION_FOR_RESOLUTION = 200704000
    const val PROP_MRSERVICE = "sys.pxr.mrserivce.user"
    const val PROP_SCREEN_STATE = "debug.tracing.screen_state"
    const val NOTIFICATION_CHANNEL_DEFAULT = "default_channel"
    val IGNORE_ENVIRONMENTS = listOf("default_scene", "AIGC", "Emulator", "Default")
}