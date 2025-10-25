package com.mok.finmsg.mixar.service.mapper;

import com.mok.finmsg.mixar.model.mt.MT202;
import com.mok.finmsg.mixar.model.mx.pacs009.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Maps SWIFT MT202 (FI-to-FI Transfer) to ISO 20022 pacs.009.001.09
 */
@Component
public class Mt202ToPacs009Mapper {

    public PacsDocument convert(MT202 mt202) {
        if (mt202 == null) throw new IllegalArgumentException("MT202 cannot be null");

        // Group Header
        GroupHeader groupHeader = new GroupHeader();
        groupHeader.setMsgId(mt202.getField20());
        groupHeader.setCreDtTm(LocalDateTime.now().toString());
        groupHeader.setNbOfTxs("1");

        BigDecimal ctrlSum = getCtrlSum(mt202);
        String currency = extractCurrency(mt202.getField32A());
        groupHeader.setCtrlSum(ctrlSum);
        groupHeader.setTtlIntrBkSttlmAmt(new AmountWithCurrency(ctrlSum, currency));
        groupHeader.setIntrBkSttlmDt(extractDate(mt202.getField32A()));
        groupHeader.setSttlmInf(new SettlementInfo());

        PaymentTypeInformation pmtTypeInfo = new PaymentTypeInformation();
        pmtTypeInfo.setInstrPrty("NORM");
        groupHeader.setPmtTpInf(pmtTypeInfo);

        // Agents (Instg / Instd)
        BranchAndFinancialInstitutionIdentification instgAgt = buildAgent(mt202.getField52A(), "UNKNOWN-INSTG");
        BranchAndFinancialInstitutionIdentification instdAgt = buildAgent(mt202.getField58A(), "UNKNOWN-INSTD");

        // Transaction
        CreditTransferTransactionFI tx = new CreditTransferTransactionFI();

        // PaymentIds
        PaymentIdentification pmtId = new PaymentIdentification();
        pmtId.setInstrId(mt202.getField20());
        pmtId.setEndToEndId(mt202.getField21());
        tx.setPmtId(pmtId);

        // Amount & Date
        tx.setIntrBkSttlmAmt(new AmountWithCurrency(ctrlSum, currency));
        tx.setIntrBkSttlmDt(extractDate(mt202.getField32A()));

        // Instg / Instd
        tx.setInstgAgt(instgAgt);
        tx.setInstdAgt(instdAgt);

        // Optional intermediaries
        if (mt202.getField56A() != null) {
            tx.setIntrmyAgt1(buildAgent(mt202.getField56A(), "UNKNOWN-INTRMY1"));
        } else {
            tx.setIntrmyAgt1(null);
        }
        tx.setIntrmyAgt1Acct(null);
        tx.setIntrmyAgt2(null);
        tx.setIntrmyAgt2Acct(null);
        tx.setIntrmyAgt3(null);
        tx.setIntrmyAgt3Acct(null);

        // Determine sending BIC (Debtor FI)
        String sendingBic = extractBic(mt202.getField52A());
        if (sendingBic == null) sendingBic = "CITIUS33XXX";

        // Set UltmtDbtr and Dbtr as FIN INST (FinInstnId) — BEFORE DbtrAgt/CdtrAgt
        BranchAndFinancialInstitutionIdentification sendingFinInst = buildAgent(sendingBic, "UNKNOWN-DBTR");
        tx.setUltmtDbtr(sendingFinInst);
        tx.setDbtr(sendingFinInst);

        // DbtrAcct (optional) — placeholder so many validators accept the element
        Account dbtrAcct = Account.builder()
                .id(AccountIdentification.builder()
                        .othr(OtherAccount.builder().id("UNKNOWN-DBTR-ACCT").build())
                        .build())
                .build();
        tx.setDbtrAcct(dbtrAcct);
        // If you prefer no placeholder: tx.setDbtrAcct(null);

        // Now set DbtrAgt (after Dbtr/DbtrAcct)
        tx.setDbtrAgt(buildAgent(sendingBic, "UNKNOWN-DBTR"));
        tx.setDbtrAgtAcct(null);

        // Creditor agent from MT58A/57A or fallback
        String creditorBic = extractBic(mt202.getField58A());
        if (creditorBic == null) creditorBic = extractBic(mt202.getField57A());
        if (creditorBic == null) creditorBic = "BNPAFRPPXXX";

        tx.setCdtrAgt(buildAgent(creditorBic, "UNKNOWN-CDTR"));

        // Option: set CdtrAgtAcct OR Cdtr. We'll set Cdtr as FIN INST (schema expects BranchAndFinancialInstitutionIdentification)
        tx.setCdtrAgtAcct(null);
        tx.setCdtr(buildAgent(creditorBic, "UNKNOWN-CDTR")); // <- IMPORTANT: use buildAgent, not Party

        // Optional other fields
        tx.setPurp(null);
        tx.setRgltryRptg(null);
        tx.setSplmtryData(null);

        // Build pacs.009
        FICdtTrf fiCdtTrf = new FICdtTrf();
        fiCdtTrf.setGrpHdr(groupHeader);
        fiCdtTrf.setCdtTrfTxInf(Collections.singletonList(tx));

        PacsDocument doc = new PacsDocument();
        doc.setFiCdtTrf(fiCdtTrf);
        return doc;
    }

    private BranchAndFinancialInstitutionIdentification buildAgent(String bicOrRaw, String fallback) {
        String bic = extractBic(bicOrRaw);

        FinancialInstitutionIdentification finInstId = FinancialInstitutionIdentification.builder()
                .bicfi(bic) // may be null
                .build();

        return BranchAndFinancialInstitutionIdentification.builder()
                .finInstnId(finInstId)
                .build();
    }

    private BigDecimal getCtrlSum(MT202 mt202) {
        String field32A = mt202.getField32A();
        if (field32A != null && field32A.length() >= 9) {
            String amountStr = field32A.substring(9).replace(',', '.');
            try {
                return new BigDecimal(amountStr);
            } catch (NumberFormatException ignored) {
            }
        }
        return BigDecimal.ZERO;
    }

    private String extractCurrency(String field32A) {
        return (field32A != null && field32A.length() >= 9) ? field32A.substring(6, 9) : "XXX";
    }

    private String extractDate(String field32A) {
        if (field32A != null && field32A.length() >= 6) {
            String d = field32A.substring(0, 6);
            return "20" + d.substring(0, 2) + "-" + d.substring(2, 4) + "-" + d.substring(4, 6);
        }
        return java.time.LocalDate.now().toString();
    }

    private String extractBic(String raw) {
        if (raw == null) return null;
        String s = raw.replaceAll("[\\r\\n\\s]+", " ").trim();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\b([A-Z0-9]{8,11})\\b").matcher(s);
        return m.find() ? m.group(1) : null;
    }
    }
