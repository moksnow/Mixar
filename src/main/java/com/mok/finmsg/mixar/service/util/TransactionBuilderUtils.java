package com.mok.finmsg.mixar.service.util;

import com.mok.finmsg.mixar.model.mx.pacs009.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author m.khandan
 * 10/30/2025
 */
public final class TransactionBuilderUtils {

    private TransactionBuilderUtils() {
    }

    public static GroupHeader buildGroupHeader(String msgId, String field32A) {
        BigDecimal amt = SwiftFieldUtils.extractAmount(field32A);
        String ccy = SwiftFieldUtils.extractCurrency(field32A, "XXX");

        GroupHeader gh = new GroupHeader();
        gh.setMsgId(msgId);
        gh.setCreDtTm(LocalDateTime.now().toString());
        gh.setNbOfTxs("1");
        gh.setCtrlSum(amt);
        gh.setTtlIntrBkSttlmAmt(new AmountWithCurrency(amt, ccy));
        gh.setIntrBkSttlmDt(SwiftFieldUtils.extractDate(field32A));
        gh.setSttlmInf(new SettlementInfo());

        PaymentTypeInformation pti = new PaymentTypeInformation();
        pti.setInstrPrty("NORM");
        gh.setPmtTpInf(pti);

        return gh;
    }

    public static CreditTransferTransactionFI baseTransaction(String instrId, String endToEndId,
                                                              GroupHeader gh, String field32A) {
        BigDecimal amt = SwiftFieldUtils.extractAmount(field32A);
        String currency = SwiftFieldUtils.extractCurrency(field32A, "XXX");

        CreditTransferTransactionFI tx = new CreditTransferTransactionFI();
        PaymentIdentification pmtId = new PaymentIdentification();
        pmtId.setInstrId(instrId);
        pmtId.setEndToEndId(endToEndId);
        tx.setPmtId(pmtId);
        tx.setIntrBkSttlmAmt(new AmountWithCurrency(amt, currency));
        tx.setIntrBkSttlmDt(gh.getIntrBkSttlmDt());
        return tx;
    }

    public static void setIntermediaries(CreditTransferTransactionFI tx, String field56A) {
        if (field56A != null)
            tx.setIntrmyAgt1(SwiftFieldUtils.buildAgent009(field56A));

        tx.setIntrmyAgt1Acct(null);
        tx.setIntrmyAgt2(null);
        tx.setIntrmyAgt2Acct(null);
        tx.setIntrmyAgt3(null);
        tx.setIntrmyAgt3Acct(null);
    }

    public static void setFiDebtorCreditor(CreditTransferTransactionFI tx,
                                           String field52A, String field58A, String field57A) {

        String sendingBic = SwiftFieldUtils.extractBic(field52A);
        if (sendingBic == null) sendingBic = "CITIUS33XXX";

        BranchAndFinancialInstitutionIdentification sendingFin =
                SwiftFieldUtils.buildAgent009(sendingBic);
        tx.setUltmtDbtr(sendingFin);
        tx.setDbtr(sendingFin);
        tx.setDbtrAcct(buildOtherAccount("UNKNOWN-DBTR-ACCT"));
        tx.setDbtrAgt(sendingFin);

        String creditorBic = SwiftFieldUtils.extractBic(field58A);
        if (creditorBic == null) creditorBic = SwiftFieldUtils.extractBic(field57A);
        if (creditorBic == null) creditorBic = "BNPAFRPPXXX";

        BranchAndFinancialInstitutionIdentification cdtrAgt =
                SwiftFieldUtils.buildAgent009(creditorBic);
        tx.setCdtrAgt(cdtrAgt);
        tx.setCdtr(cdtrAgt);
    }

    public static Account buildOtherAccount(String fallback) {
        return Account.builder()
                .id(AccountIdentification.builder()
                        .othr(OtherAccount.builder().id(fallback).build())
                        .build())
                .build();
    }
}

