package org.jarling.models.transactions;

/**
 *
 * @author Nav Roudsari (nav@rzari.co.uk)
 *
 */
public enum TransactionType {

    FIRST_PAYMENT_OF_DIRECT_DEBIT("FIRST_PAYMENT_OF_DIRECT_DEBIT"),
    DEBIT_ROUTING_DATA_MACHINE_READ_FROM_MAGNETIC_STRIP("DEBIT_ROUTING_DATA_MACHINE_READ_FROM_MAGNETIC_STRIP"),
    CLAIM_FOR_UNPAID_CHEQUE("CLAIM_FOR_UNPAID_CHEQUE"),
    DIRECT_DEBIT("DIRECT_DEBIT"),
    DIRECT_DEBIT_REPRESENTATION("DIRECT_DEBIT_REPRESENTATION"),
    DIRECT_DEBIT_FINAL_PAYMENT("DIRECT_DEBIT_FINAL_PAYMENT"),
    INTER_BANK_SETTLEMENT("INTER_BANK_SETTLEMENT"),
    BACS_CREDIT("BACS_CREDIT"),
    CHARGE_TO_CREDIT_CARD("CHARGE_TO_CREDIT_CARD"),
    REFUND_TO_CREDIT_CARD("REFUND_TO_CREDIT_CARD"),
    UNPAID_DIRECT_DEBIT_FIRST_PAYMENT("UNPAID_DIRECT_DEBIT_FIRST_PAYMENT"),
    UNPAID_DIRECT_DEBIT("UNPAID_DIRECT_DEBIT"),
    UNPAID_DIRECT_DEBIT_REPRESENTATION("UNPAID_DIRECT_DEBIT_REPRESENTATION"),
    UNPAID_DIRECT_DEBIT_FINAL_PAYMENT("UNPAID_DIRECT_DEBIT_FINAL_PAYMENT"),
    BUILDING_SOCIETY_INTEREST_CREDIT("BUILDING_SOCIETY_INTEREST_CREDIT"),
    DIVIDENDS_INTEREST("DIVIDENDS_INTEREST"),
    CREDIT_RETURNED_UN_APPLIED("CREDIT_RETURNED_UN_APPLIED");

    private final String value;

    TransactionType(String value){ this.value = value; }

    public String getValue() {
        return this.value;
    }
}