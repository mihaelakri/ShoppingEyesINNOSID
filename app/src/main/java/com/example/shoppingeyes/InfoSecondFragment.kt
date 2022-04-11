package com.example.shoppingeyes

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.shoppingeyes.databinding.FragmentInfoFirstBinding
import com.example.shoppingeyes.databinding.FragmentInfoSecondBinding
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoSecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoSecondFragment : Fragment(), TextToSpeech.OnInitListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var tts: TextToSpeech? = null
    private lateinit var session: SharedPrefs
    private lateinit var binding: FragmentInfoSecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentInfoSecondBinding.inflate(layoutInflater)
        session = SharedPrefs(requireActivity().applicationContext)

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
        val view = inflater.inflate(R.layout.fragment_info_second, container, false)

        val img: ImageView = view.findViewById<View>(R.id.imageView3) as ImageView
        val title: TextView = view.findViewById<View>(R.id.textView2) as TextView
        val text: TextView = view.findViewById<View>(R.id.textView5) as TextView

        if(session.getTheme() == "SecondTheme"){
            img.setImageResource(R.drawable.uputa2contrast)
            title.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.light_green))
            text.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.light_green))
        }else{
            img.setImageResource(R.drawable.upute2a)
            title.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.black))
            text.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.black))
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InfoSecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InfoSecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS && session.getSound()) {
            val result = tts!!.setLanguage(Locale.US)
            val textRead3: TextView = binding.textView2
            val textRead4: TextView = binding.textView5
            tts!!.speak(textRead3.text.toString(), TextToSpeech.QUEUE_ADD, null, "")
            tts!!.speak(textRead4.text.toString(), TextToSpeech.QUEUE_ADD, null, "")
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(activity, "Language specified not supported", Toast.LENGTH_SHORT).show()
            }
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