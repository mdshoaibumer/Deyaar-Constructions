package com.example.domain.model

data class Transaction(
    val id: String,
    val projectId: String,
    val date: Long,
    val time: Long,
    val type: TransactionType,
    val category: TransactionCategory,
    val amountPaise: Long, // Amount in paise (1 INR = 100 paise) for financial precision
    val paymentMethod: PaymentMethod,
    val referenceNumber: String?,
    val description: String?,
    val createdBy: String,
    val isDeleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val attachmentPath: String?
)

enum class TransactionType {
    INCOME, EXPENSE, ADJUSTMENT
}

enum class TransactionCategory {
    CLIENT_ADVANCE, CLIENT_PAYMENT, MATERIAL_PURCHASE, LABOUR_PAYMENT, 
    EQUIPMENT_RENT, TRANSPORT, FUEL, FOOD, SITE_EXPENSE, OFFICE_EXPENSE, 
    REFUND, ADJUSTMENT, MISCELLANEOUS
}

enum class PaymentMethod {
    CASH, UPI, BANK_TRANSFER, CHEQUE, CREDIT_CARD, DEBIT_CARD, OTHER
}
