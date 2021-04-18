package com.example.emergency.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emergency.databinding.FragmentHistoryBinding
import com.example.emergency.model.HistoryViewModel
import com.example.emergency.model.STATUS
import com.example.emergency.util.BaseFragment
import com.example.emergency.util.showMessage
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class HistoryFragment : BaseFragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val historyAdapter = HistoryAdapter()
        with(binding) {
            with(recyclerView) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = historyAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
            historyViewModel.status.observe(viewLifecycleOwner) {
                when (it) {
                    STATUS.History.REFRESH_COMPLETE -> {
                        swipeRefresh.isRefreshing = false
                    }
                    STATUS.History.REFRESH_ERROR -> {
                        swipeRefresh.isRefreshing = false
                        showMessage(requireContext(), historyViewModel.errorMessage)
                    }
                    null -> {
                    }
                }
            }



            historyViewModel.historyList.observe(viewLifecycleOwner) {
                historyAdapter.submitList(it)
            }

            swipeRefresh.setOnRefreshListener {
                historyViewModel.refreshHistory()
            }
        }

    }

}