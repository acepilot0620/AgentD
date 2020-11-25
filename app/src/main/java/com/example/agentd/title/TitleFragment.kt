package com.example.agentd.title

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.agentd.R
import com.example.agentd.databinding.FragmentTitleBinding

class TitleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentTitleBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_title, container, false)

        val viewModelFactory = TitleViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        val titleViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(TitleViewModel::class.java)

        binding.titleViewModel = titleViewModel

        binding.setLifecycleOwner(this)

        val navController = (activity as AppCompatActivity).findNavController(R.id.nav_host_fragment_title)

        titleViewModel.navigateToUser.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
            }
        })



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}