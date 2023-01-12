package kobramob.rubeg38.ru.myprotection

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val router: Router by inject()
    private val navigatorHolder: NavigatorHolder by inject()
    private val navigator = AppNavigator(this, R.id.fragmentContainer)

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
    }

    override fun onResume() {
        navigatorHolder.setNavigator(navigator)
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

}