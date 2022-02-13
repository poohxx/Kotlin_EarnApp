package com.poohxx.earnapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.poohxx.earnapp.adapters.CategoryAdapter
import com.poohxx.earnapp.adapters.ContentManager
import com.poohxx.earnapp.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity(), CategoryAdapter.Listener {
    private lateinit var binding: ActivityMainBinding
    private var adapter: CategoryAdapter? = null
    private var interAd: InterstitialAd? = null
    private var timer: CountDownTimer? = null
    private var posM:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
                initAdMob()
        (application as AppMainState).showAdIfAvailable(this) {}
        initRcView()
        binding.imBg.setOnClickListener {
            getResult()
        }

    }

    private fun initRcView() = with(binding) {
        adapter = CategoryAdapter(this@MainActivity)
        rcViewCat.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        rcViewCat.adapter = adapter
        adapter?.submitList(ContentManager.list)
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
        loadInterAd()
    }

    override fun onPause() {
        super.onPause()
        binding.adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.adView.destroy()
    }
   private fun getResult(){
       var counter = 0
       timer?.cancel()
        timer=object : CountDownTimer(2000,20){
            override fun onTick(p0: Long) {
                counter++
                if (counter>14)counter =0
                binding.imBg.setImageResource(MainConst.imageList[counter])

            }

            override fun onFinish() {
                getMessage()

            }

        }.start()
    }

    private fun initAdMob() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun loadInterAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interAd = ad
                }
            })

    }

    private fun showInterAd() {
        if (interAd != null) {
            interAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    showContent()
                    interAd = null
                    loadInterAd()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    showContent()
                    interAd = null
                    loadInterAd()
                }

                override fun onAdShowedFullScreenContent() {

                    interAd = null
                    loadInterAd()
                }
            }
            interAd?.show(this)
        } else {
            showContent()
        }
    }

    private fun showContent() {
        Toast.makeText(this, "content content", Toast.LENGTH_SHORT).show()
    }
    private fun getMessage() = with(binding){
        val currentArray = resources.getStringArray(MainConst.arrayList[posM])
        val message = currentArray[Random.nextInt(currentArray.size)]
        val messageList = message.split("|")
        tvContent.text = messageList[1]
        tvName.text=messageList[0]
        imBg.setImageResource(MainConst.imageList[Random.nextInt(14)])
    }

    override fun onClick(pos: Int) {
        posM=pos
        getResult()
    }
}