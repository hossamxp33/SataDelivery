package com.sata.satadelivery.presentation.current_order_fragment

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.sata.satadelivery.R
import com.sata.satadelivery.databinding.CurrentOrderFragmentBinding
import com.sata.satadelivery.helper.BaseApplication
import com.sata.satadelivery.helper.UserError
import com.sata.satadelivery.presentation.current_order_fragment.adapter.CurrentOrdersAdapter
import com.sata.satadelivery.presentation.current_order_fragment.mvi.CurrentOrderViewModel
import com.sata.satadelivery.presentation.current_order_fragment.mvi.MainIntent

import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class CurrentOrderFragment @Inject constructor(var viewModel:CurrentOrderViewModel) : DialogFragment() {

    companion object { const val TAG = "TownBottomSheetDialogFragment" }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory



    private val appViewModel: CurrentOrderViewModel by viewModels()




    lateinit var currentOrdersAdapter: CurrentOrdersAdapter


    lateinit var view: CurrentOrderFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseApplication.appComponent.inject(this)
        setStyle(STYLE_NO_FRAME, R.style.colorPickerStyle);

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        view = DataBindingUtil.inflate(inflater,
            R.layout.current_order_fragment, container, false)

     //   view.listener = ClickHandler()
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE);
        dialog!!.setCanceledOnTouchOutside(true);
        viewModel.intents.trySend(MainIntent.Initialize(viewModel.state.value!!))
        getAllData()


        view.dismissBtn.setOnClickListener {
            this.dismiss()
        }

        currentOrderRecycleView()
        return view.root
    }
    fun currentOrderRecycleView() {
        currentOrdersAdapter = CurrentOrdersAdapter(viewModel.intents, requireContext(),this,viewModel)
        view.dailyOrderRecycle.apply {
            adapter = currentOrdersAdapter
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
        }
    }

    private fun getAllData() {

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                if (it != null) {
                    if (it.error != null) {
                        it.error?.let {
                            when (it) {
                                is UserError.InvalidId -> "Invalid id"
                                is UserError.NetworkError -> it.throwable.message
                                UserError.ServerError -> "Server error"
                                UserError.Unexpected -> "Unexpected error"
                                is UserError.UserNotFound -> "User not found"
                                UserError.ValidationFailed -> "Validation failed"
                            }
                        }
                        viewModel.intents.send(MainIntent.ErrorDisplayed(it))
                    } else {
                        if (it.progress == true) {
                            view.progress.isVisible =  true
                        } else {
                            view.progress.visibility = View.GONE
                            if (!it.data.isNullOrEmpty()) {
                                currentOrdersAdapter.submitList(it.data)
                                view.noOrderFound.isVisible = false

//                                var mp = MediaPlayer.create(requireContext(), R.raw.alarm);
//                                mp.start();

                            }else{
                                view.progress.isVisible = true
                                view.noOrderFound.isVisible = true
                                viewModel.intents.send(MainIntent.Initialize(it))
                            }

                        }

                    }
                }
            }
        }
    }



    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = 1800
        dialog!!.window!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        if (savedInstanceState == null) {
            dialog.window?.setWindowAnimations(
                R.style.Animation_Design_BottomSheetDialog
            )
        } else {
            dialog.window?.setWindowAnimations(
                R.style.Animation_Design_BottomSheetDialog
            )
        }


        return dialog
    }




}