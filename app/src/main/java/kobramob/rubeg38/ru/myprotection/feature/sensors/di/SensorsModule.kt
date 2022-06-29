package kobramob.rubeg38.ru.myprotection.feature.sensors.di

import kobramob.rubeg38.ru.myprotection.feature.sensors.ui.SensorsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sensorsModule = module {
    viewModel<SensorsViewModel> {
        SensorsViewModel()
    }
}