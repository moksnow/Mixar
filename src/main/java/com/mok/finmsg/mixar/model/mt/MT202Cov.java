package com.mok.finmsg.mixar.model.mt;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author M_Khandan
 * Date: 10/29/2025
 * Time: 2:00 PM
 */


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MT202Cov extends MT202 {

    private String field50A; // :50a: Ordering Customer
    private String field50K; // :50K: Ordering Customer (alternate format)
    private String field59; // :59: Beneficiary Customer
    private String field70; // :70: Remittance Information / Payment Details
    private String field72Cov; // :72: Sender to Receiver Information
    private String field52AOverride; // Optional: :52A: Ordering Institution override (can differ from 202 base)
    private String field21Cov; // Optional: Related Reference :21: if differs from base

}
