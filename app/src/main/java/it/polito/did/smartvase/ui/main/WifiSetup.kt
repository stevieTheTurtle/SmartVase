package it.polito.did.smartvase.ui.main

import android.Manifest
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import it.polito.did.smartvase.MainActivity
import it.polito.did.smartvase.R


@Suppress("DEPRECATION")
class WifiSetup : Fragment() {

    private val viewModel: MainViewModel by activityViewModels<MainViewModel>()
    lateinit var wifiManager: WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide)
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.wifi_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val browserButton = view.findViewById<Button>(R.id.browserButton6)
        val back = view.findViewById<Button>(R.id.back_button6)
        val next = view.findViewById<Button>(R.id.next_button6)

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))

                //viewModel.plantIcon=resources.getDrawable(R.drawable.nficusicon)
        browserButton.setOnClickListener {
            viewModel.connected=true //TODO startActivity(browserIntent)
        }


        back.setOnClickListener {goBack() }
        //TODO AUTOMATICAMENTE A PAGINA SUCCESSIVA
        next.setOnClickListener {
            if (!viewModel.connected /*TODO CONTROLLO DATABASE VLADDD*/) {
                val snack = Snackbar.make(it, "Open Browser and connect to WiFi", Snackbar.LENGTH_SHORT)
                snack.show()
            } else {
                if(viewModel.plantCreated) {
                    val snack = Snackbar.make(it, "Already connected", Snackbar.LENGTH_LONG)
                    snack.show()
                }
                else
                    findNavController().navigate(R.id.action_wifisetup_to_plantsetup)
            }
        }
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        when {
            ContextCompat.checkSelfPermission(
                (activity as MainActivity),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                //TODO
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        val requestPermissionLauncher2 =
            registerForActivityResult(ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        when {
            ContextCompat.checkSelfPermission(
                (activity as MainActivity),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                //TODO
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher2.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
        view.findViewById<TextView>(R.id.franco6).setText(enableWiFi())
    }

    fun goBack(){findNavController().navigate(R.id.action_wifisetup_to_homepage)}
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    // Leave empty do disable back press or
                    // write your code which you want
                    goBack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    private fun enableWiFi() : String{
        wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        Log.d("mamma", wifiManager.connectionInfo.toString())
        return wifiManager.connectionInfo.macAddress
    }
    fun getMac(): String {
        val wifiMgr = getApplicationContext<Context>().getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        return wifiInfo.toString()
    }

}
