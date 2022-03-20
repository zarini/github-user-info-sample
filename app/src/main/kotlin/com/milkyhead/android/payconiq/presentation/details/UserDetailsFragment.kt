package com.milkyhead.android.payconiq.presentation.details

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.milkyhead.android.payconiq.R
import com.milkyhead.android.payconiq.core.Constants
import com.milkyhead.android.payconiq.core.afterMeasured
import com.milkyhead.android.payconiq.core.gone
import com.milkyhead.android.payconiq.core.show
import com.milkyhead.android.payconiq.databinding.FragmentUserDetailsBinding
import com.milkyhead.android.payconiq.domain.model.UserDetailsModel
import com.milkyhead.android.payconiq.presentation.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
internal class UserDetailsFragment : BaseFragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<UserDetailsViewModel>()

    private var errorMessage: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(
            context ?: return
        ).inflateTransition(android.R.transition.move)

        if (savedInstanceState == null) {
            val username = arguments?.getString(Constants.KEY_USER_NAME)
                ?: throw IllegalArgumentException("username must be passed")
            viewModel.getUserDetails(username)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        extractUserdata()
        actions()
        observe()

        binding.userDetailsInfoContainer.afterMeasured {
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                translationY = view.height.toFloat()
            } else {
                translationX = view.width.toFloat()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressed() {
        if (errorMessage?.isShownOrQueued == true) {
            errorMessage?.dismiss()
        }
        findNavController().popBackStack()
    }

    private fun extractUserdata() {
        val avatar = arguments?.getString(Constants.KEY_USER_AVATAR)
        binding.userDetailsAvatar.load(avatar)

        val username = arguments?.getString(Constants.KEY_USER_NAME)
        binding.userDetailsUsername.text = username
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    when {
                        state.loading -> {
                            binding.userDetailsLoading.show()
                        }

                        state.result != null -> {
                            binding.userDetailsLoading.gone()
                            bindUserdata(state.result)
                        }

                        state.error != null -> {
                            binding.userDetailsLoading.gone()
                            errorMessage = Snackbar.make(
                                binding.root,
                                state.error,
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction(getString(R.string.retry)) {
                                val username = arguments?.getString(Constants.KEY_USER_NAME)
                                    ?: return@setAction
                                viewModel.getUserDetails(username)
                            }
                            errorMessage?.show()
                        }
                    }
                }
            }
        }
    }

    private fun actions() {
        binding.userDetailsBack.setOnClickListener {
            onBackPressed()
        }
        binding.userDetailsUrl.setOnClickListener {
            try {
                val url = binding.userDetailsUrl.text.toString()
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun bindUserdata(userDetailsModel: UserDetailsModel) {
        binding.userDetailsFullName.text = userDetailsModel.fullName ?: ""
        binding.userDetailsFollowingCount.text = userDetailsModel.following.toString()
        binding.userDetailsFollowersCount.text = userDetailsModel.followers.toString()
        binding.userDetailsRepoCount.text = userDetailsModel.publicRepoCount.toString()
        binding.userDetailsUrl.text = userDetailsModel.url

        if (userDetailsModel.bio.isNullOrBlank()) {
            binding.userDetailsBio.gone()
        } else {
            binding.userDetailsBio.text = userDetailsModel.bio
        }

        val location = buildString {
            if (userDetailsModel.location != null && userDetailsModel.location != "null") {
                append(userDetailsModel.location)
            }
            if (userDetailsModel.company != null && userDetailsModel.company != "null") {
                if (length > 0) append(", ")
                append(userDetailsModel.company)
            }
        }
        binding.userDetailsLocation.text = location

        showInformationWithAnimation()
    }

    private fun showInformationWithAnimation() {
        val animation = binding.userDetailsInfoContainer
            .animate()
            .setDuration(400)
            .setInterpolator(DecelerateInterpolator())

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            animation.translationY(0f)
        } else {
            animation.translationX(0f)
        }
        animation.start()
    }
}