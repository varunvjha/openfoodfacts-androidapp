package openfoodfacts.github.scrachx.openfood.test

object ScreenshotsLocaleProvider {
    fun getFilteredParameters(predicate: (ScreenshotParameter?) -> Boolean = { true }) =
            ScreenshotParametersProvider.defaults.filter(predicate)
}