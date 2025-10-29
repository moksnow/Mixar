package com.mok.finmsg.mixar.model.mt;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author m.khandan
 * 10/23/2025
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MT103 extends MtMessage {
    private String field20; // transaction reference
    private String field32A; // value date, currency, amount (YYMMDDCURAMT)
    private String field50; // ordering customer
    private String field50Acct; // Ordering customer account IBAN
    private String field59; // beneficiary customer
    private String field59Acct; // Beneficiary account IBAN
    // optional ordering institution / sender correspondent fields
    private String field52A;    // :52A: (BIC) // Ordering Institution (BIC)
    private String field52D;    // :52D: (name/address variant)
    // optional account with / beneficiary agent fields
    private String field57A;    // :57A: (BIC) // Account With Institution (BIC)
    private String field57D;    // :57D: (name/address variant)
}
