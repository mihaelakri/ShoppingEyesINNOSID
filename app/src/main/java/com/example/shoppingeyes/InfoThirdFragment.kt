package com.example.shoppingeyes

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.shoppingeyes.databinding.FragmentInfoSecondBinding
import com.example.shoppingeyes.databinding.FragmentInfoThirdBinding
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoThridFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoThirdFragment : Fragment(), TextToSpeech.OnInitListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var tts: TextToSpeech? = null
    private lateinit var binding: FragmentInfoThirdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentInfoThirdBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        tts = TextToSpeech(activity,this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_third, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InfoThridFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InfoThirdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts!!.setLanguage(Locale.US)
            val textRead5: TextView = binding.textView6
            val textRead6: TextView = binding.textView7
            tts!!.speak(textRead5.getText().toString(), TextToSpeech.QUEUE_ADD, null, "")
            tts!!.speak(textRead6.getText().toString(), TextToSpeech.QUEUE_ADD, null, "")
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(activity, "Language specified not supported", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}