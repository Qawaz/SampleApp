package com.wakaztahir.sample.controller

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.wakaztahir.mindnode.controller.modules.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class AppState @Inject constructor(
    @ApplicationContext val context : Context,
    val settings: AppSettings,
    val removeAds: RemoveAdsProduct,
    @ApplicationScope
    val scope : CoroutineScope,
) {

    var themeType : AppSettings.ThemeType by mutableStateOf(settings.getThemeType())
    private set

    var interstitialAd : InterstitialAd? = null
    val isFirstTime = settings.getIsFirstTime().also {
        if(it) settings.setIsFirstTime(true)
    }

    var isProMember by mutableStateOf(settings.getIsProMember())
    private set

    init {
        removeAds.purchaseListener = { verifyProMembership() }
        verifyProMembership()
    }

    @Composable
    fun isDarkTheme() : Boolean {
        return when(themeType){
            AppSettings.ThemeType.Light -> false
            AppSettings.ThemeType.Dark -> true
            AppSettings.ThemeType.System -> isSystemInDarkTheme()
        }
    }

    fun updateThemeType(themeType: AppSettings.ThemeType){
        this.themeType = themeType
        settings.setThemeType(themeType)
    }

    private fun verifyProMembership(){
        scope.launch {
            isProMember = removeAds.isPurchased().also {
                settings.setIsProMember(it)
            }
        }
    }

    fun loadAd(onLoaded : (InterstitialAd)->Unit,onShown : ()->Unit){
        if(isProMember) return
        if(interstitialAd != null){
            onLoaded(interstitialAd!!)
            return
        }
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context,"ca-app-pub-7864431654199564/4582116600", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                this@AppState.interstitialAd = interstitialAd
                interstitialAd.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        this@AppState.interstitialAd = null
                        onShown()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        this@AppState.interstitialAd = null
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                    }
                }
                onLoaded(interstitialAd)
            }
        })
    }

    suspend fun displayRating(activity: Activity) : Boolean {
        val lastTimeRated = settings.getLastTimeRatedApp()
        if (lastTimeRated > System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 10)) {
            return false
        }
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        val reviewInfo = suspendCoroutine<ReviewInfo?> { continuation ->
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result)
                } else continuation.resume(null)
            }
        } ?: return false
        val flow = manager.launchReviewFlow(activity, reviewInfo)
        return suspendCoroutine { continuation ->
            flow.addOnCompleteListener {
                settings.setLastTimeRatedApp(System.currentTimeMillis())
                continuation.resume(it.isSuccessful)
            }
        }
    }

}