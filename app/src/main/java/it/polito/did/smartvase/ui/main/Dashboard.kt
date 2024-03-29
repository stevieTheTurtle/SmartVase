package it.polito.did. smartvase.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import it.polito.did.smartvase.MainActivity
import it.polito.did.smartvase.R
import kotlin.system.measureTimeMillis

class Dashboard : Fragment() {

    private val viewModel: MainViewModel by activityViewModels<MainViewModel>()

    fun Long.toBoolean() = this>0
    fun getDataFromDB(){
        viewModel.db.child("plants").child(viewModel.plantMacAddress).child("name").get()
            .addOnSuccessListener {
                viewModel.plantName = it.value.toString()
                Log.d("caccaaaaa",viewModel.plantName)
            }.addOnFailureListener {
                Toast.makeText(context, "Error getting data from DB", Toast.LENGTH_SHORT).show()
            }

        viewModel.db.child("plants").child(viewModel.plantMacAddress).child("soilMoisture")
            .get().addOnSuccessListener {
                viewModel.soilMoisture = (it.value as Double).toFloat()
            }.addOnFailureListener {
                Toast.makeText(context, "Error getting data from DB", Toast.LENGTH_SHORT).show()
            }

        viewModel.db.child("plants").child(viewModel.plantMacAddress).child("soilMoistureMin")
            .get().addOnSuccessListener {
                viewModel.defaultMin = (it.value as Double).toFloat()
            }.addOnFailureListener {
                Toast.makeText(context, "Error getting data from DB", Toast.LENGTH_SHORT).show()
            }

        viewModel.db.child("plants").child(viewModel.plantMacAddress).child("soilMoistureMax")
            .get().addOnSuccessListener {
                viewModel.defaultMax = (it.value as Double).toFloat()
            }.addOnFailureListener {
                Toast.makeText(context, "Error getting data from DB", Toast.LENGTH_SHORT).show()
            }

        viewModel.db.child("plants").child(viewModel.plantMacAddress).child("waterLevel").get()
            .addOnSuccessListener {
                viewModel.waterLevel = (it.value as Double).toFloat()
            }.addOnFailureListener {
                Toast.makeText(context, "Error getting data from DB", Toast.LENGTH_SHORT).show()
            }

        viewModel.db.child("plants").child(viewModel.plantMacAddress).child("imagePlant").get()
            .addOnSuccessListener {
                viewModel.plantIconId = (it.value as Long).toInt()
            }.addOnFailureListener {
                Toast.makeText(context, "Error getting data from DB", Toast.LENGTH_SHORT).show()
            }

        viewModel.db.child("plants").child(viewModel.plantMacAddress).child("notify_mode").get()
            .addOnSuccessListener {
                viewModel.notification = (it.value as Long).toBoolean()
            }.addOnFailureListener {
                Toast.makeText(context, "Error getting data from DB", Toast.LENGTH_SHORT).show()
            }

        viewModel.db.child("plants").child(viewModel.plantMacAddress).child("auto_mode").get()
            .addOnSuccessListener {
                viewModel.auto = (it.value as Long).toBoolean()
                Log.d("panettone", "ue buono")
                if(viewModel.porcata) {
                    viewModel.porcata=false
                    refreshFrag()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Error getting data from DB", Toast.LENGTH_SHORT).show()
            }

    }
    fun refreshFrag(){findNavController().navigate(R.id.action_dashboard_to_dashboard)}

    var elapsed : Long =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
        exitTransition = inflater.inflateTransition(R.transition.fade)
        elapsed = measureTimeMillis {
            getDataFromDB()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dashboard, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val waterBar = view.findViewById<CardView>(R.id.waterLevelBar2)
        val soilMostureBar = view.findViewById<CardView>(R.id.soilMoistureBar2)
        val plantName = view.findViewById<TextView>(R.id.plantName2)
        val plantIcon = view.findViewById<ImageView>(R.id.PlantIcon2)
        val offText = view.findViewById<TextView>(R.id.offText2)
        val homeButton = view.findViewById<ImageButton>(R.id.homeButton2)
        val editButton = view.findViewById<ImageButton>(R.id.editButton2)
        val autoWaterButton = view.findViewById<FloatingActionButton>(R.id.autoWaterButton2)
        val notificationButton = view.findViewById<ImageButton>(R.id.notificationButton2)
        val notificationState = view.findViewById<TextView>(R.id.notificationState2)
        val SoilMoisturePercentage = view.findViewById<TextView>(R.id.SoilMoisturePercentage)
        val WaterLevelPercentage = view.findViewById<TextView>(R.id.WaterLevelPercentage)
        val dividerMaxSoilMoisture = view.findViewById<ProgressBar>(R.id.dividerMaxSoilMoisture2)
        val dividerMinSoilMoisture = view.findViewById<ProgressBar>(R.id.dividerMinSoilMoisture2)
        val soilAlert = view.findViewById<ImageView>(R.id.soilAlert2)
        val waterAlert = view.findViewById<ImageView>(R.id.waterAlert2)

        val plants = FirebaseDatabase.getInstance().getReference("plants")

        val refresh = view.findViewById<ImageButton>(R.id.refresh2)
        val loading = view.findViewById<ConstraintLayout>(R.id.loading2)

        val waterMax = 0.15

        if(viewModel.porcata) {
            loading.visibility = View.VISIBLE
            //Timer().schedule(1000){loading.visibility=View.GONE}
        }
        else
            loading.visibility=View.GONE

        val barHeight=waterBar.translationY
        if(viewModel.waterLevel<.10) {
            waterAlert.visibility = View.VISIBLE
            if(viewModel.notification) (activity as MainActivity).notification(viewModel.plantIconId,"Please load some water",(waterMax * viewModel.waterLevel).toString().substring(0,4) + " L left")
        }
        if(viewModel.soilMoisture<viewModel.defaultMin) {
            if(viewModel.notification) (activity as MainActivity).notification(viewModel.plantIconId,"Please water this plant",(viewModel.soilMoisture*100).toInt().toString()+"% Soil moisture")
            soilAlert.visibility = View.VISIBLE
        }
        if(viewModel.soilMoisture>viewModel.defaultMax) {
            if(viewModel.notification) (activity as MainActivity).notification(viewModel.plantIconId,"Too much water",(viewModel.soilMoisture*100).toInt().toString()+"% Soil moisture")
            soilAlert.visibility = View.VISIBLE
        }

        //popolamento viste
        plantName?.setText(viewModel.plantName)
        plantIcon?.setImageResource(viewModel.plantIconId)

        //testo percentuale riempimento
        WaterLevelPercentage?.text= (waterMax*viewModel.waterLevel).toString().substring(0,4)+"L"
        SoilMoisturePercentage?.text= (viewModel.soilMoisture*100).toInt().toString()+"%"

        //divisori barre
        dividerMaxSoilMoisture?.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 1-viewModel.defaultMax  }
        dividerMinSoilMoisture?.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 1-viewModel.defaultMin  }

        //riempire barre
        waterBar.translationY +=  (1-viewModel.waterLevel) *  barHeight
        soilMostureBar.translationY += (1-viewModel.soilMoisture) * barHeight

        if(viewModel.auto){
            offText?.text="ON"
            offText?.setTextColor(Color.RED)
        }
        if(!viewModel.notification){
            notification(notificationButton,notificationState,false)
        }
        refresh.setOnClickListener { refreshFrag() }
        homeButton.setOnClickListener { goBack() }
        editButton.setOnClickListener { findNavController().navigate(R.id.action_dashboard_to_settings) } //ancora da capire come fare, se con altro fragment

        autoWaterButton.setOnLongClickListener{
            viewModel.auto = !viewModel.auto
            (activity as MainActivity).vibration(true)
            if(viewModel.auto) {
                waterButton(autoWaterButton, offText, true)
                viewModel.db.child("plants").child(viewModel.plantMacAddress).child("auto_mode").setValue(1)
            }
            else {
                waterButton(autoWaterButton, offText, false)
                viewModel.db.child("plants").child(viewModel.plantMacAddress).child("auto_mode").setValue(0)
            }
            return@setOnLongClickListener true
        }

        autoWaterButton.setOnClickListener{
            viewModel.db.child("plants").child(viewModel.plantMacAddress).child("to_water_control").setValue(1)
        }

        notificationButton.setOnClickListener {
            viewModel.notification = !viewModel.notification
            notification(notificationButton,notificationState,viewModel.notification)
            (activity as MainActivity).vibration(true)
            viewModel.db.child("plants").child(viewModel.plantMacAddress).child("notify_mode").setValue(viewModel.notification.toInt())
        }
    }

    fun Boolean.toInt() = if (this) 1 else 0

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    fun waterButton(waterButton:ImageButton, waterState:TextView, state:Boolean){
        if(state) {
            waterButton.foreground=resources.getDrawable(R.drawable.waterbuttonon)
            waterState.setText("ON")
        }
        else{
            waterButton.foreground=resources.getDrawable(R.drawable.waterbuttonoff)
            waterState.setText("OFF")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    fun notification(notificationButton:ImageButton, notificationState:TextView, state:Boolean){
        if(state) {
            notificationButton.foreground=resources.getDrawable(R.drawable.notificationonbutton)
            notificationState.setText("ON")
        }
        else{
            notificationButton.foreground=resources.getDrawable(R.drawable.notificationoffbutton)
            notificationState.setText("OFF")
        }
    }
    fun goBack(){
        viewModel.porcata=true
        findNavController().navigate(R.id.action_dashboard_to_homepage)
    }
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
}