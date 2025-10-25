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
    /** :20: Transaction Reference Number */
    private String field20;

    /** :21: Related Reference */
    private String field21;

    /** :32A: Value Date (YYMMDD), Currency, Amount */
    private String field32A;

    /** :52A: Ordering Institution (Debtor Agent) */
    private String field52A;

    /** :53A: Sender’s Correspondent (optional) */
    private String field53A;

    /** :56A: Intermediary Institution (optional) */
    private String field56A;

    /** :57A: Account With Institution (Creditor Agent) */
    private String field57A;

    /** :58A: Beneficiary Institution (Receiving Bank) */
    private String field58A;

    /** :72: Sender to Receiver Information (optional) */
    private String field72;

}
