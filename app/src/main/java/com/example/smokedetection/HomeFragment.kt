package com.example.smokedetection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smokedetection.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.hide()
        binding.cardDataset.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, DatasetFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.cardArchitecture.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, ModelArchitectureFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.cardFeatures.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, AboutFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.cardSimulation.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, SimulationFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        (activity as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.show()
        super.onDestroyView()
        _binding = null
    }
}