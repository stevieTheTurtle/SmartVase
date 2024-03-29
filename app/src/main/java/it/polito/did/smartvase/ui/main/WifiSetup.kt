package it.polito.did.smartvase.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var browserButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        browserButton = view.findViewById<Button>(R.id.browserButton6)
        val back = view.findViewById<Button>(R.id.back_button6)
        val next = view.findViewById<Button>(R.id.next_button6)
        val connect = view.findViewById<Button>(R.id.connect6)

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.msftconnecttest.com/redirect"))

        browserButton.setOnClickListener {
            startActivity(browserIntent)
        }
        if(!wifiConnected())
            browserButton.visibility=View.INVISIBLE

        connect.setOnClickListener {
            startActivity( Intent(Settings.ACTION_WIFI_SETTINGS))
            viewModel.wifiPage=true
        }

        back.setOnClickListener {goBack() }
        next.setOnClickListener {
            if (wifiConnected()) {
                viewModel.db.child("plants").child(viewModel.plantName).get().addOnSuccessListener {
                    if (it.value == null)
                        Log.d("cosucce", "Connettiti scemo")
                    if (viewModel.plantCreated) {
                        val snack = Snackbar.make(view, "Already connected", Snackbar.LENGTH_LONG)
                        snack.show()
                    } else {
                        viewModel.plantName="Plant Name"
                        viewModel.plantIconId= android.R.drawable.toast_frame
                        findNavController().navigate(R.id.action_wifisetup_to_plantsetup)
                    }
                }.addOnFailureListener {
                    val snack = Snackbar.make(view, "DB Connection Lost.", Snackbar.LENGTH_SHORT)
                    snack.show()
                    Log.d("cosucce", "Connettiti scemo")
                }

                /*if (viewModel.db.child("plants").child(viewModel.plantMacAddress.toString()).get().equals(null)) {
                val snack = Snackbar.make(it, "Connect to Wi-Fi and complete the setup", Snackbar.LENGTH_SHORT)
                snack.show()
            } else {
                if(viewModel.plantCreated) {
                    val snack = Snackbar.make(it, "Already connected", Snackbar.LENGTH_LONG)
                    snack.show()
                }
                else
                    findNavController().navigate(R.id.action_wifisetup_to_plantsetup)
            }*/
            }else   {
                val snack = Snackbar.make(it, "Connect to Wi-Fi and complete the setup", Snackbar.LENGTH_SHORT)
                snack.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(wifiConnected())
            browserButton.visibility=View.VISIBLE
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

    private fun wifiConnected() : Boolean {
        //if(getMac()=="SmartVase")
        /*if (getSSid().substring(1, 9) == "SmartVas")
        {
            //viewModel.plantMacAddress=getSSid().replace(":","").substring(10)
            return true
        }*/
        return viewModel.wifiPage
    }
    private fun getSSid() : String{
        wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo.ssid
    }
}
