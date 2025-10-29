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
public class MT202 extends MtMessage {
    private String field20; // :20: Transaction Reference Number
    private String field21; // :21: Related Reference
    private String field32A; // :32A: Value Date (YYMMDD), Currency, Amount
    private String field52A; // :52A: Ordering Institution (Debtor Agent)
    private String field53A; // :53A: Sender’s Correspondent (optional)
    private String field56A; // :56A: Intermediary Institution (optional)
    private String field57A; // :57A: Account With Institution (Creditor Agent)
    private String field58A; // :58A: Beneficiary Institution (Receiving Bank)
    private String field72; // Sender to Receiver Information (optional)
}
