package kobramob.rubeg38.ru.myprotection

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject

private const val NOTIFICATIONS_PERMISSION_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {

    private val router: Router by inject()
    private val navigatorHolder: NavigatorHolder by inject()
    private val navigator = AppNavigator(this, R.id.fragmentContainer)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Snackbar.make(
                window.decorView.rootView,
                R.string.notifications_permission_not_granted_message,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when(resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)){
            Configuration.UI_MODE_NIGHT_YES->{
                window?.statusBarColor = getColor(R.color.darkthemebackgroundcolor)
            }
            Configuration.UI_MODE_NIGHT_NO->{
                window?.statusBarColor = getColor(R.color.lightthemenostatusbarcolor)
            }
            else->{
                window?.statusBarColor = getColor(R.color.darkthemebackgroundcolor)
            }
        }

        router.newRootScreen(Screens.splashscreen())
        askNotificationPermission()
    }

    override fun onResume() {
        navigatorHolder.setNavigator(navigator)
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                applicationContext.checkSelfPermission(POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {}
                shouldShowRequestPermissionRationale(POST_NOTIFICATIONS) -> {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.permission_required_title)
                        .setMessage(R.string.permission_required_message)
                        .setPositiveButton(R.string.grant_permission_button_text) { _, _ ->
                            requestPermissions(arrayOf(POST_NOTIFICATIONS), NOTIFICATIONS_PERMISSION_REQUEST_CODE)
                        }
                        .setNegativeButton(R.string.refuse_permission_button_text) { _, _ ->
                            Snackbar.make(
                                window.decorView.rootView,
                                R.string.notifications_permission_not_granted_message,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                }
                else -> {
                    requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

}