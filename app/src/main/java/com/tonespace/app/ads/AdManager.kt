package com.tonespace.app.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.tonespace.app.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor() {

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    var isAdMobAvailable = false
        private set

    fun initialize(context: Context) {
        try {
            Class.forName("com.google.android.gms.ads.MobileAds")
            isAdMobAvailable = true
            Log.d("AdManager", "AdMob available")
        } catch (e: Exception) {
            isAdMobAvailable = false
            Log.d("AdManager", "AdMob not available - running without ads")
        }
    }

    fun loadBanner(adView: AdView) {
        if (!isAdMobAvailable) return
        try {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        } catch (e: Exception) {
            Log.e("AdManager", "Failed to load banner", e)
        }
    }

    fun loadInterstitial(context: Context) {
        if (!isAdMobAvailable) return
        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                Constants.ADMOB_INTERSTITIAL_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                interstitialAd = null
                                loadInterstitial(context)
                            }
                        }
                    }
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("AdManager", "Failed to load interstitial", e)
        }
    }

    fun showInterstitial(activity: Activity, onDismissed: (() -> Unit)? = null) {
        if (!isAdMobAvailable) {
            onDismissed?.invoke()
            return
        }
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onDismissed?.invoke()
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    onDismissed?.invoke()
                }
            }
            ad.show(activity)
        } else {
            onDismissed?.invoke()
        }
    }

    fun loadRewardedAd(context: Context, onLoaded: (() -> Unit)? = null) {
        if (!isAdMobAvailable) return
        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                Constants.ADMOB_REWARDED_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                rewardedAd = null
                                loadRewardedAd(context)
                            }
                        }
                        onLoaded?.invoke()
                    }
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAd = null
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("AdManager", "Failed to load rewarded ad", e)
        }
    }

    fun showRewardedAd(
        activity: Activity,
        onReward: (rewardAmount: Int, rewardType: String) -> Unit,
        onDismissed: (() -> Unit)? = null
    ) {
        if (!isAdMobAvailable) {
            onDismissed?.invoke()
            return
        }
        val ad = rewardedAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd(activity)
                    onDismissed?.invoke()
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                    onDismissed?.invoke()
                }
            }
            ad.show(activity) { rewardItem ->
                onReward(rewardItem.amount, rewardItem.type)
            }
        } else {
            onDismissed?.invoke()
        }
    }

    fun isInterstitialLoaded(): Boolean = isAdMobAvailable && interstitialAd != null
    fun isRewardedLoaded(): Boolean = isAdMobAvailable && rewardedAd != null
}