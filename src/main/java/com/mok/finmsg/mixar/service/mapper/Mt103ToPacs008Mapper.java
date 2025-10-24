package com.mok.finmsg.mixar.service.mapper;

import com.mok.finmsg.mixar.model.mt.MT103;
import com.mok.finmsg.mixar.model.mx.pacs008.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Maps SWIFT MT103 (Customer Credit Transfer) to ISO 20022 pacs.008.001.12
 */
@Component
public class Mt103ToPacs008Mapper {

    public PacsDocument convert(MT103 mt103) {
        if (mt103 == null) throw new IllegalArgumentException("MT103 cannot be null");

        // --- Group Header ---
        GroupHeader groupHeader = new GroupHeader();
        groupHeader.setMsgId(mt103.getField20());
        groupHeader.setCreDtTm(LocalDateTime.now().toString());
        groupHeader.setNbOfTxs("1");

        BigDecimal ctrlSum = getCtrlSum(mt103);
        String currency = extractCurrency(mt103.getField32A());

        if (ctrlSum != null) {
            groupHeader.setCtrlSum(ctrlSum);
            groupHeader.setTtlIntrBkSttlmAmt(new AmountWithCurrency(ctrlSum, currency));
        }

        groupHeader.setIntrBkSttlmDt(extractDate(mt103.getField32A()));
        groupHeader.setSttlmInf(new SettlementInfo());

        PaymentTypeInformation pmtTypeInfo = new PaymentTypeInformation();
        pmtTypeInfo.setInstrPrty("NORM");
        groupHeader.setPmtTpInf(pmtTypeInfo);

        // --- Agents ---
        String instgBic = extractBic(mt103.getField52A());
        groupHeader.setInstgAgt(buildAgent(instgBic, extractBankName(mt103.getField52D()), "UNKNOWN-INSTG-AGT"));

        String instdBic = extractBic(mt103.getField57A());
        groupHeader.setInstdAgt(buildAgent(instdBic, extractBankName(mt103.getField57D()), "UNKNOWN-INSTD-AGT"));

        // --- Parties ---
        Party debtor = new Party(mt103.getField50(), null, null, null);
        Party creditor = new Party(mt103.getField59(), null, null, null);

        // --- Credit Transfer Transaction ---
        CreditTransferTransaction tx = new CreditTransferTransaction();

// Payment Identification
        PaymentIdentification pmtId = new PaymentIdentification();
        pmtId.setInstrId(mt103.getField20());
        pmtId.setEndToEndId(mt103.getField20());
        tx.setPmtId(pmtId);

        // Interbank Amount & Date
        tx.setIntrBkSttlmAmt(new AmountWithCurrency(ctrlSum, currency));
        tx.setIntrBkSttlmDt(extractDate(mt103.getField32A()));
        tx.setChrgBr("SLEV"); // optional

        // --- Parties ---

        // --- Debtor Account ---
        Account dbtrAcct;
        if (mt103.getField50Acct() != null && !mt103.getField50Acct().isEmpty()) {
            dbtrAcct = Account.builder()
                    .id(AccountIdentification.builder().iban(mt103.getField50Acct()).build())
                    .build();
        } else {
            dbtrAcct = Account.builder()
                    .id(AccountIdentification.builder()
                            .othr(OtherAccount.builder().id("UNKNOWN-DBTR-ACCT").build())
                            .build())
                    .build();
        }

        // --- Creditor Account ---
        Account cdtrAcct;
        if (mt103.getField59Acct() != null && !mt103.getField59Acct().isEmpty()) {
            cdtrAcct = Account.builder()
                    .id(AccountIdentification.builder().iban(mt103.getField59Acct()).build())
                    .build();
        } else {
            cdtrAcct = Account.builder()
                    .id(AccountIdentification.builder()
                            .othr(OtherAccount.builder().id("UNKNOWN-CDTR-ACCT").build())
                            .build())
                    .build();
        }


        tx.setDbtr(debtor);
        tx.setDbtrAcct(dbtrAcct);
        tx.setDbtrAgt(groupHeader.getInstgAgt());
//        tx.setDbtrAgtAcct(null);  // optional, can be null

        tx.setCdtrAgt(groupHeader.getInstdAgt());
        tx.setCdtr(creditor);
        tx.setCdtrAcct(cdtrAcct);

// --- set all optional elements to null if not available ---
        tx.setDbtrAgtAcct(null);
        tx.setUltmtDbtr(null);
        tx.setInstrForCdtrAgt(null);
        tx.setInstrForNxtAgt(null);
        tx.setPurp(null);
        tx.setRgltryRptg(null);
        tx.setTax(null);
        tx.setRltdRmtInf(null);
        tx.setRmtInf(null);
        tx.setSplmtryData(null);

        tx.setInitgPty(debtor);

//

        // --- Assemble final FIToFICstmrCdtTrf ---
        FIToFICstmrCdtTrf cdtTrf = new FIToFICstmrCdtTrf();
        cdtTrf.setGrpHdr(groupHeader);
        cdtTrf.setCdtTrfTxInf(Collections.singletonList(tx));

        PacsDocument document = new PacsDocument();
        document.setFiToFICstmrCdtTrf(cdtTrf);

        return document;
    }

    private BranchAndFinancialInstitutionIdentification buildAgent(String bic, String name, String fallback) {
        if (bic != null)
            return new BranchAndFinancialInstitutionIdentification(new FinancialInstitutionIdentification(bic, null));
        if (name != null)
            return new BranchAndFinancialInstitutionIdentification(new FinancialInstitutionIdentification(null, name));
        return new BranchAndFinancialInstitutionIdentification(new FinancialInstitutionIdentification(null, fallback));
    }

    private BigDecimal getCtrlSum(MT103 mt103) {
        String field32A = mt103.getField32A();
        if (field32A != null && field32A.length() >= 9) {
            String amountStr = field32A.substring(9).replace(',', '.');
            try {
                return new BigDecimal(amountStr);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private String extractCurrency(String field32A) {
        if (field32A != null && field32A.length() >= 9) return field32A.substring(6, 9);
        return null;
    }

    private String extractDate(String field32A) {
        if (field32A != null && field32A.length() >= 6) {
            String d = field32A.substring(0, 6);
            return "20" + d.substring(0, 2) + "-" + d.substring(2, 4) + "-" + d.substring(4, 6);
        }
        return LocalDate.now().toString();
    }

    private String extractBic(String raw) {
        if (raw == null) return null;
        String s = raw.replaceAll("[\\r\\n\\s]+", " ").trim();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\b([A-Z0-9]{8,11})\\b").matcher(s);
        if (m.find()) return m.group(1);
        if (s.length() >= 4 && s.length() <= 11) return s;
        return null;
    }

    private String extractBankName(String raw) {
        if (raw == null) return null;
        String[] lines = raw.replace("\r", "").split("\n");
        for (String line : lines) {
            String s = line.trim();
            if (!s.isEmpty() && !s.matches("^[A-Z0-9]{8,11}$")) return s;
        }
        return null;
    }
}
