package com.example.smokedetection

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.smokedetection.databinding.FragmentSimulationBinding

class SimulationFragment : Fragment() {

    private val TAG = "SmokeDetection"

    private var _binding: FragmentSimulationBinding? = null
    private val binding get() = _binding!!

    private lateinit var modelHelper: TFLiteModelHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimulationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = "Simulasi"
        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        modelHelper = TFLiteModelHelper(requireContext())
        binding.predictButton.setOnClickListener { predictSmoke() }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun predictSmoke() {
        binding.predictButton.isEnabled = false
        try {
            var hasError = false
            val inputs = listOf(
                binding.inputUTC,
                binding.inputTemperature,
                binding.inputHumidity,
                binding.inputTVOC,
                binding.inputECO2,
                binding.inputRawH2,
                binding.inputRawEthanol,
                binding.inputPressure,
                binding.inputPM10,
                binding.inputPM25,
                binding.inputNC05,
                binding.inputNC10,
                binding.inputNC25,
            )
            val inputValues = inputs.map { input ->
                val value = input.text.toString().toFloatOrNull()
                if (value == null) {
                    input.error = "Invalid input"
                    hasError = true
                    0f
                } else {
                    input.error = null
                    value
                }
            }.toFloatArray()
            if (hasError) {
                Toast.makeText(requireContext(), "Periksa kembali input!", Toast.LENGTH_SHORT).show()
                return
            }
            val prediction = modelHelper.predict(inputValues)
            val resultText = if (prediction > 0.5f) {
                binding.resultTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                "🚨 Smoke Detected (${String.format(java.util.Locale.getDefault(), "%.2f", prediction)})"
            } else {
                binding.resultTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                "✅ No Smoke Detected (${String.format(java.util.Locale.getDefault(), "%.2f", prediction)})"
            }
            binding.resultTextView.text = resultText
            Log.d(TAG, resultText)

        } catch (e: Exception) {
            Log.e(TAG, "Error during prediction", e)
            Toast.makeText(
                requireContext(),
                "Terjadi kesalahan saat prediksi.",
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            binding.predictButton.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}