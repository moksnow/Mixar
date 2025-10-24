package com.mok.finmsg.mixar.service.mapper;

import com.mok.finmsg.mixar.model.mt.MT202;
import com.mok.finmsg.mixar.model.mx.pacs009.GroupHeader;
import com.mok.finmsg.mixar.model.mx.pacs009.PacsDocument;
import com.mok.finmsg.mixar.model.mx.pacs009.Party;
import com.mok.finmsg.mixar.model.mx.pacs009.PaymentInstruction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Maps SWIFT MT202 (FI-to-FI Transfer) to ISO 20022 pacs.009.001.09
 */
@Component
public class Mt202ToPacs009Mapper {

    public PacsDocument convert(MT202 mt202) {
        if (mt202 == null) {
            throw new IllegalArgumentException("MT202 cannot be null");
        }

        // --- Group Header ---
        GroupHeader groupHeader = new GroupHeader();
        groupHeader.setMsgId(mt202.getField20());
        groupHeader.setCreDtTm(LocalDateTime.now().toString());

        // --- Payment Instruction ---
        PaymentInstruction paymentInstruction = new PaymentInstruction();

        // Parse Field32A (YYMMDDCURAMT)
        String field32A = mt202.getField32A();
        if (field32A != null && field32A.length() >= 9) {
            String currency = field32A.substring(6, 9);
            String amount = field32A.substring(9);
            paymentInstruction.setInstdAmt(amount + " " + currency);
        } else {
            paymentInstruction.setInstdAmt(field32A);
        }

        // Debtor (Sending Institution)
        Party debtor = new Party();
        debtor.setName(mt202.getField21()); // Optional related reference
        paymentInstruction.setDbtr(debtor);

        // Creditor (Receiving Institution)
        Party creditor = new Party();
        creditor.setName(mt202.getField58A());
        paymentInstruction.setCdtr(creditor);

        // --- Assemble Final Document ---
        PacsDocument document = new PacsDocument();
        document.setGrpHdr(groupHeader);
        document.setPmtInf(paymentInstruction);

        return document;
    }
}
