package com.mok.finmsg.mixar.service.mapper;

import com.mok.finmsg.mixar.model.mt.MT103;
import com.mok.finmsg.mixar.model.mx.pacs008.*;
import com.mok.finmsg.mixar.service.util.SwiftFieldUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Maps SWIFT MT103 to ISO20022 pacs.008 (clean, modular, and testable).
 *
 * Author: m.khandan
 * Date: 10/25/2025
 */
@Component
public class Mt103ToPacs008Mapper {

    public PacsDocument convert(MT103 mt103) {
        if (mt103 == null) {
            throw new IllegalArgumentException("MT103 cannot be null");
        }

        GroupHeader header = buildGroupHeader(mt103);
        String currency = SwiftFieldUtils.extractCurrency(mt103.getField32A(), null);

        BranchAndFinancialInstitutionIdentification instgAgent = buildAgent(
                mt103.getField52A(), mt103.getField52D(), "UNKNOWN-INSTG-AGT"
        );
        BranchAndFinancialInstitutionIdentification instdAgent = buildAgent(
                mt103.getField57A(), mt103.getField57D(), "UNKNOWN-INSTD-AGT"
        );

        header.setInstgAgt(instgAgent);
        header.setInstdAgt(instdAgent);

        CreditTransferTransaction transaction = buildTransaction(mt103, header, currency, instgAgent, instdAgent);

        FIToFICstmrCdtTrf body = new FIToFICstmrCdtTrf();
        body.setGrpHdr(header);
        body.setCdtTrfTxInf(Collections.singletonList(transaction));

        PacsDocument document = new PacsDocument();
        document.setFiToFICstmrCdtTrf(body);
        return document;
    }

    private GroupHeader buildGroupHeader(MT103 mt103) {
        BigDecimal amount = SwiftFieldUtils.extractAmount(mt103.getField32A());
        String currency = SwiftFieldUtils.extractCurrency(mt103.getField32A(), "USD");

        GroupHeader header = new GroupHeader();
        header.setMsgId(mt103.getField20());
        header.setCreDtTm(LocalDateTime.now().toString());
        header.setNbOfTxs("1");

        header.setCtrlSum(amount);
        header.setTtlIntrBkSttlmAmt(new AmountWithCurrency(amount, currency));
        header.setIntrBkSttlmDt(SwiftFieldUtils.extractDate(mt103.getField32A()));

        header.setSttlmInf(new SettlementInfo());
        header.setPmtTpInf(new PaymentTypeInformation("NORM"));

        return header;
    }

    private BranchAndFinancialInstitutionIdentification buildAgent(String bicField, String nameField, String fallback) {
        return SwiftFieldUtils.buildAgent008(
                SwiftFieldUtils.extractBic(bicField),
                SwiftFieldUtils.extractBankName(nameField),
                fallback
        );
    }

    private CreditTransferTransaction buildTransaction(
            MT103 mt103,
            GroupHeader header,
            String currency,
            BranchAndFinancialInstitutionIdentification instgAgent,
            BranchAndFinancialInstitutionIdentification instdAgent
    ) {
        CreditTransferTransaction tx = new CreditTransferTransaction();

        // Identification
        tx.setPmtId(new PaymentIdentification(mt103.getField20(), mt103.getField20()));

        // Amount
        tx.setIntrBkSttlmAmt(new AmountWithCurrency(header.getCtrlSum(), currency));
        tx.setIntrBkSttlmDt(header.getIntrBkSttlmDt());
        tx.setChrgBr("SLEV");

        // Parties
        Party debtor = new Party(mt103.getField50(), null, null, null);
        Party creditor = new Party(mt103.getField59(), null, null, null);

        tx.setDbtr(debtor);
        tx.setDbtrAcct(buildAccount(mt103.getField50Acct(), "UNKNOWN-DBTR-ACCT"));
        tx.setDbtrAgt(instgAgent);

        tx.setCdtr(creditor);
        tx.setCdtrAcct(buildAccount(mt103.getField59Acct(), "UNKNOWN-CDTR-ACCT"));
        tx.setCdtrAgt(instdAgent);

        // Optional / default fields
        tx.setUltmtDbtr(null);
        tx.setDbtrAgtAcct(null);
        tx.setInstrForCdtrAgt(null);
        tx.setInstrForNxtAgt(null);
        tx.setPurp(null);
        tx.setRgltryRptg(null);
        tx.setTax(null);
        tx.setRltdRmtInf(null);
        tx.setRmtInf(null);
        tx.setSplmtryData(null);
        tx.setInitgPty(debtor);

        return tx;
    }

    private Account buildAccount(String iban, String fallback) {
        if (iban != null && !iban.isBlank()) {
            return Account.builder()
                    .id(AccountIdentification.builder().iban(iban).build())
                    .build();
        }
        return Account.builder()
                .id(AccountIdentification.builder()
                        .othr(OtherAccount.builder().id(fallback).build())
                        .build())
                .build();
    }
}
