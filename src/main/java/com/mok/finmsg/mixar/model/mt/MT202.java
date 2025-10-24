package com.mok.finmsg.mixar.model.mt;

import lombok.*;

/**
 * @author m.khandan
 * 10/23/2025
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MT202 extends MtMessage {
    private String field20; // transaction reference
    private String field21; // related reference
    private String field32A; // value date, currency, amount
    private String field58A; // receiving institution
}
