package com.openclassrooms.realestatemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlin.math.pow

class LoanViewModel(application: Application) : AndroidViewModel(application) {

    fun calculateLoanRefund(
        loanAmount: Float,
        interestRate: Float,
        insuranceRate: Float,
        durationMonths: Float
    ): Float {
        return (loanAmount * ((interestRate + insuranceRate) / 1200)) /
                (1 - (1 + ((interestRate + insuranceRate) / 1200)).pow(-durationMonths))
    }

}