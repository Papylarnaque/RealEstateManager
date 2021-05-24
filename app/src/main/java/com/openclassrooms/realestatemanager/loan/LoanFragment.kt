package com.openclassrooms.realestatemanager.loan

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.slider.Slider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentLoanBinding
import com.openclassrooms.realestatemanager.utils.formatPrice
import com.openclassrooms.realestatemanager.viewmodel.LoanViewModel
import java.text.NumberFormat
import java.util.*

class LoanFragment : DialogFragment(R.layout.fragment_loan), Slider.OnChangeListener {

    private lateinit var viewModel: LoanViewModel
    private lateinit var binding: FragmentLoanBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoanBinding.inflate(layoutInflater)
        viewModel = LoanViewModel(Application())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialogSize()
        setActions()
        setUpLoanAmountSlider()
        setUpInterestRateSlider()
        setUpInsuranceRateSlider()
        setUpLoanDurationSlider()
    }

    private fun setActions() {
        binding.loanReturn.setOnClickListener {
            dismiss()
        }
    }

    private fun setDialogSize() {
        if (requireContext().resources.getBoolean(R.bool.isTablet)) {
            dialog?.window?.setLayout(
                1200,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        } else {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        updateUI()
    }

    private fun formatDollars(float: Float): String {
        return NumberFormat.getCurrencyInstance(Locale.US).run {
            maximumFractionDigits = 0
            format(float)
        }
    }

    private fun formatPercent(float: Float): String {
        return NumberFormat.getPercentInstance().run {
            maximumFractionDigits = 2
            format(float / 100)
        }
    }

    private fun formatNoDigits(float: Float): String {
        return NumberFormat.getInstance().run {
            maximumFractionDigits = 0
            format(float)
        }
    }

    private fun setUpLoanAmountSlider() {
        binding.loanAmountSlider.apply {
            setLabelFormatter { formatPrice(it.toInt()) }
            addOnChangeListener(this@LoanFragment)
            value = 1000000f
            valueFrom = 100000f
            valueTo = 10000000f
        }
    }

    private fun setUpInterestRateSlider() {
        binding.loanInterestSlider.apply {
            setLabelFormatter { formatPercent(it) }
            addOnChangeListener(this@LoanFragment)
            value = 1.2f
        }
    }

    private fun setUpInsuranceRateSlider() {
        binding.loanInsuranceSlider.apply {
            setLabelFormatter { formatPercent(it) }
            addOnChangeListener(this@LoanFragment)
            value = 0.3f
        }
    }

    private fun setUpLoanDurationSlider() {
        binding.loanDurationSlider.apply {
            setLabelFormatter { formatNoDigits(it) }
            addOnChangeListener(this@LoanFragment)
            value = 20f
        }
    }

    private fun updateUI() {
        binding.loanAmountDisplayTextView.text = formatDollars(binding.loanAmountSlider.value)
        binding.loanInterestAmountTextview.text = formatPercent(binding.loanInterestSlider.value)
        binding.loanInsuranceAmountTextview.text = formatPercent(binding.loanInsuranceSlider.value)
        binding.loanDurationAmountTextview.text = formatNoDigits(binding.loanDurationSlider.value)

        val result = viewModel.calculateLoanRefund(
            binding.loanAmountSlider.value,
            binding.loanInterestSlider.value,
            binding.loanInsuranceSlider.value,
            binding.loanDurationSlider.value * 12,
        )

        binding.monthlyPaymentResultTextView.text =
            NumberFormat.getCurrencyInstance(Locale.US).run {
                maximumFractionDigits = 2
                format(result)
            }
    }

    override fun onPause() {
        dismiss()
        super.onPause()
    }

}