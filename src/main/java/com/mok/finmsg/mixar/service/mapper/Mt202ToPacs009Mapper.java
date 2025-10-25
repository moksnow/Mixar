package com.mok.finmsg.mixar.service.mapper;

import com.mok.finmsg.mixar.model.mt.MT202;
import com.mok.finmsg.mixar.model.mx.pacs009.*;
import com.mok.finmsg.mixar.service.util.SwiftFieldUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Maps SWIFT MT202 -> ISO20022 pacs.009 (clean, uses SwiftFieldUtils)
 */
@Component
public class Mt202ToPacs009Mapper {

    public PacsDocument convert(MT202 mt202) {
        if (mt202 == null) throw new IllegalArgumentException("MT202 cannot be null");

        GroupHeader gh = buildGroupHeader(mt202);

        CreditTransferTransactionFI tx = buildTransaction(mt202, gh);

        FICdtTrf body = new FICdtTrf();
        body.setGrpHdr(gh);
        body.setCdtTrfTxInf(Collections.singletonList(tx));

        PacsDocument doc = new PacsDocument();
        doc.setFiCdtTrf(body);
        return doc;
    }

    private GroupHeader buildGroupHeader(MT202 mt202) {
        BigDecimal amt = SwiftFieldUtils.extractAmount(mt202.getField32A());
        String ccy = SwiftFieldUtils.extractCurrency(mt202.getField32A(), "XXX");

        GroupHeader gh = new GroupHeader();
        gh.setMsgId(mt202.getField20());
        gh.setCreDtTm(LocalDateTime.now().toString());
        gh.setNbOfTxs("1");
        gh.setCtrlSum(amt);
        gh.setTtlIntrBkSttlmAmt(new AmountWithCurrency(amt, ccy));
        gh.setIntrBkSttlmDt(SwiftFieldUtils.extractDate(mt202.getField32A()));
        gh.setSttlmInf(new SettlementInfo());

        PaymentTypeInformation pti = new PaymentTypeInformation();
        pti.setInstrPrty("NORM");
        gh.setPmtTpInf(pti);

        return gh;
    }

    private CreditTransferTransactionFI buildTransaction(MT202 mt202, GroupHeader gh) {
        BigDecimal amt = gh.getCtrlSum();
        String currency = SwiftFieldUtils.extractCurrency(mt202.getField32A(), "XXX");

        CreditTransferTransactionFI tx = new CreditTransferTransactionFI();

        tx.setPmtId(new PaymentIdentification(mt202.getField20(), mt202.getField21()));
        tx.setIntrBkSttlmAmt(new AmountWithCurrency(amt, currency));
        tx.setIntrBkSttlmDt(gh.getIntrBkSttlmDt());

        // instg/instd (pacs009 typed builders)
        tx.setInstgAgt(SwiftFieldUtils.buildAgent009(mt202.getField52A()));
        tx.setInstdAgt(SwiftFieldUtils.buildAgent009(mt202.getField58A()));

        // optional intermed
        if (mt202.getField56A() != null) {
            tx.setIntrmyAgt1(SwiftFieldUtils.buildAgent009(mt202.getField56A()));
        } else {
            tx.setIntrmyAgt1(null);
        }
        tx.setIntrmyAgt1Acct(null);
        tx.setIntrmyAgt2(null);
        tx.setIntrmyAgt2Acct(null);
        tx.setIntrmyAgt3(null);
        tx.setIntrmyAgt3Acct(null);

        // Debtor / UltmtDbtr as FIN INST (must be FinInstnId wrappers for FI-to-FI)
        String sendingBic = SwiftFieldUtils.extractBic(mt202.getField52A());
        if (sendingBic == null) sendingBic = "CITIUS33XXX";

        com.mok.finmsg.mixar.model.mx.pacs009.BranchAndFinancialInstitutionIdentification sendingFin =
                SwiftFieldUtils.buildAgent009(sendingBic);
        tx.setUltmtDbtr(sendingFin);
        tx.setDbtr(sendingFin);

        // Dbtr account (placeholder or null)
        tx.setDbtrAcct(buildOtherAccount("UNKNOWN-DBTR-ACCT"));

        // DbtrAgt (after Dbtr/DbtrAcct)
        tx.setDbtrAgt(SwiftFieldUtils.buildAgent009(sendingBic));
        tx.setDbtrAgtAcct(null);

        // Creditor (agent + party as FIN INST)
        String creditorBic = SwiftFieldUtils.extractBic(mt202.getField58A());
        if (creditorBic == null) creditorBic = SwiftFieldUtils.extractBic(mt202.getField57A());
        if (creditorBic == null) creditorBic = "BNPAFRPPXXX";

        com.mok.finmsg.mixar.model.mx.pacs009.BranchAndFinancialInstitutionIdentification cdtrAgt =
                SwiftFieldUtils.buildAgent009(creditorBic);
        tx.setCdtrAgt(cdtrAgt);

        // set either CdtrAgtAcct or Cdtr (we set Cdtr as FIN INST to satisfy schema)
        tx.setCdtrAgtAcct(null);
        tx.setCdtr(cdtrAgt);

        tx.setPurp(null);
        tx.setRgltryRptg(null);
        tx.setSplmtryData(null);

        return tx;
    }

    private com.mok.finmsg.mixar.model.mx.pacs009.Account buildOtherAccount(String fallback) {
        return com.mok.finmsg.mixar.model.mx.pacs009.Account.builder()
                .id(com.mok.finmsg.mixar.model.mx.pacs009.AccountIdentification.builder()
                        .othr(com.mok.finmsg.mixar.model.mx.pacs009.OtherAccount.builder()
                                .id(fallback).build())
                        .build())
                .build();
    }
}
