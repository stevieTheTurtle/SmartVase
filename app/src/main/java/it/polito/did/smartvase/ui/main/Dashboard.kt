package it.polito.did. smartvase.ui.main

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import it.polito.did.smartvase.R
import it.polito.did.smartvase.ui.main.Homepage
import it.polito.did.smartvase.ui.main.MainViewModel

class Dashboard : Fragment() {

    companion object {
        fun newInstance() = Homepage()
    }

    private val viewModel: MainViewModel by activityViewModels<MainViewModel>()

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
        return inflater.inflate(R.layout.dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val waterBar = view?.findViewById<CardView>(R.id.waterLevelBar2)
        val soilMostureBar = view?.findViewById<CardView>(R.id.soilMoistureBar2)
        val plantName = view?.findViewById<TextView>(R.id.plantName2)
                                val plantIcon = view?.findViewById<ImageView>(R.id.PlantIcon2)
        val offText = view?.findViewById<TextView>(R.id.offText2)
        val homeButton = view.findViewById<ImageButton>(R.id.homeButton2)
        val editButton = view.findViewById<ImageButton>(R.id.editButton2)
        val autoWaterButton = view.findViewById<FloatingActionButton>(R.id.autoWaterButton2)
        val notificationButton = view.findViewById<ImageButton>(R.id.notificationButton2)
        val SoilMoisturePercentage = view?.findViewById<TextView>(R.id.SoilMoisturePercentage)
        val WaterLevelPercentage = view?.findViewById<TextView>(R.id.WaterLevelPercentage)
//        val dividerMaxWaterLevel = view?.findViewById<ProgressBar>(R.id.dividerMaxWaterLevel2)
//        val dividerMinWaterLevel = view?.findViewById<ProgressBar>(R.id.dividerMinWaterLevel2)
        val dividerMaxSoilMoisture = view?.findViewById<ProgressBar>(R.id.dividerMaxSoilMoisture2)
        val dividerMinSoilMoisture = view?.findViewById<ProgressBar>(R.id.dividerMinSoilMoisture2)

        val db = Firebase.database.reference //TODO VLAD TE TENGO DOCCHIOOOO
        val ref = db.child("A7/toWaterControl")

        val barHeight=waterBar.translationY

                                        //popolamento viste
        plantName?.setText(viewModel.plantName)
        plantIcon?.setImageResource(viewModel.plantIconId)

        //testo percentuale riempimento
        SoilMoisturePercentage?.text= viewModel.waterLevel.toString()
        WaterLevelPercentage?.text= viewModel.soilMoisture.toString()

        //divisori barre
//        dividerMaxWaterLevel?.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 1-viewModel.waterLevelMax  }
//        dividerMinWaterLevel?.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 1-viewModel.waterLevelMin  }
        dividerMaxSoilMoisture?.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 1-viewModel.soilMoistureMax  }
        dividerMinSoilMoisture?.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 1-viewModel.soilMoistureMin  }

        //da percentuale a valore di traslazione (385dp lunghezza barra)
        var fillWater :Float=385- 385f * viewModel.waterLevel //TODO DA DP A PIXEL DI MERDA
        var fillSoil :Float =385- 385f * viewModel.soilMoisture

        //riempire barre
        waterBar.translationY +=  viewModel.waterLevel *  barHeight
        soilMostureBar.translationY += viewModel.soilMoisture * barHeight


        if(viewModel.auto){
            offText?.text="ON"
            offText?.setTextColor(Color.RED)
        }

        homeButton.setOnClickListener { findNavController().navigate(R.id.action_dashboard_to_homepage) }
        editButton.setOnClickListener { findNavController().navigate(R.id.action_dashboard_to_settings) } //ancora da capire come fare, se con altro fragment

        autoWaterButton.setOnLongClickListener{
            viewModel.auto = !viewModel.auto
            if(viewModel.auto)
                offText?.text="ON"
            else
                offText?.text="OFF"
            ref.setValue(1) //TODO VLADDO
            // activity?.vibrate(25);
            return@setOnLongClickListener true
        }

        notificationButton.setOnClickListener {
            viewModel.notification = !viewModel.notification
        }
    }
}