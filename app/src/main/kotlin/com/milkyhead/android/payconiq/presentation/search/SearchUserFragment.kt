package com.milkyhead.android.payconiq.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.milkyhead.android.payconiq.R
import com.milkyhead.android.payconiq.core.Constants
import com.milkyhead.android.payconiq.core.doAfterTextChanged
import com.milkyhead.android.payconiq.databinding.FragmentSearchUserBinding
import com.milkyhead.android.payconiq.presentation.BaseFragment
import com.milkyhead.android.payconiq.presentation.event.SearchEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
internal class SearchUserFragment : BaseFragment() {

    private var _binding: FragmentSearchUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SearchUserViewModel>()
    private lateinit var adapter: SearchUserAdapter

    private var errorMessage: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        actions()
        observe()
    }

    override fun onBackPressed() {
        activity?.finish()
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    when {
                        state.loading -> {
                            binding.searchEmptyStateTitle.visibility = View.GONE
                            adapter.isLoading = true
                        }

                        state.result != null -> {
                            adapter.isLoading = false

                            binding.searchResultCount.text = getString(
                                R.string.search_total_count,
                                state.result.totalCount
                            )

                            if (state.clearOldData) {
                                adapter.clearData()
                                adapter.noMoreData = false
                            }

                            adapter.addSearchData(
                                state.result.users,
                                state.result.totalCount
                            )
                        }

                        state.error != null -> {
                            adapter.isLoading = false
                            errorMessage = Snackbar.make(
                                binding.root,
                                state.error,
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction(getString(R.string.retry)) {
                                viewModel.search(
                                    SearchEvent(
                                        query = binding.searchBox.text.toString(),
                                        retry = true
                                    )
                                )
                            }
                            errorMessage?.show()
                        }

                        else -> {
                            adapter.isLoading = false
                            adapter.noMoreData = false
                            adapter.clearData()
                            binding.searchResultCount.text = ""
                            dismissErrorMessage()
                            binding.searchEmptyStateTitle.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun actions() {
        viewLifecycleOwner.lifecycleScope.doAfterTextChanged(
            500L,
            binding.searchBox
        ) { query ->
            dismissErrorMessage()
            viewModel.search(
                SearchEvent(
                    query = query
                )
            )
        }

        binding.searchBox.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.search(
                    SearchEvent(
                        query = binding.searchBox.text.toString()
                    )
                )
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun initAdapter() {
        adapter = SearchUserAdapter(
            onLoadMoreListener = { itemCount ->
                viewModel.search(
                    SearchEvent(
                        query = binding.searchBox.text.toString(),
                        loadMore = true,
                        currentCount = itemCount
                    )
                )
            },
            onItemClicked = { userModel, imageView ->
                imageView.transitionName = getString(R.string.select_user_transition_name)
                val extras = FragmentNavigatorExtras(
                    imageView to getString(R.string.select_user_transition_name)
                )

                findNavController().navigate(
                    resId = R.id.action_searchUserFragment_to_userDetailsFragment,
                    args = Bundle().apply {
                        putString(Constants.KEY_USER_NAME, userModel.username)
                        userModel.avatar?.let { avatar ->
                            putString(Constants.KEY_USER_AVATAR, avatar)
                        }
                    },
                    navOptions = null,
                    navigatorExtras = extras
                )
            }
        )

        binding.searchRecyclerView.adapter = adapter
    }

    private fun dismissErrorMessage() {
        if (errorMessage?.isShownOrQueued == true) {
            errorMessage?.dismiss()
        }
    }
}