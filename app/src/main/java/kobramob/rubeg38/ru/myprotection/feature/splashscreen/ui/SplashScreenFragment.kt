package kobramob.rubeg38.ru.myprotection.feature.splashscreen.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kobramob.rubeg38.ru.myprotection.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    companion object {
        fun create() = SplashScreenFragment()
    }

    private val viewModel: SplashScreenViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onViewCreated()

        when(resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)){
            Configuration.UI_MODE_NIGHT_YES->{
                activity?.window?.statusBarColor = context?.getColor(R.color.darkthemebackgroundcolor)!!
            }
            Configuration.UI_MODE_NIGHT_NO->{
                activity?.window?.statusBarColor = context?.getColor(R.color.lightthemenostatusbarcolor)!!
            }
            else->{
                activity?.window?.statusBarColor = context?.getColor(R.color.darkthemebackgroundcolor)!!
            }
        }
    }

}