package com.mok.finmsg.mixar.service.util;

import com.mok.finmsg.mixar.model.mx.pacs008.BranchAndFinancialInstitutionIdentification;
import com.mok.finmsg.mixar.model.mx.pacs008.FinancialInstitutionIdentification;
import com.mok.finmsg.mixar.model.mx.pacs008.Party;
import com.mok.finmsg.mixar.model.mx.pacs009.RemittanceInformation;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author m.khandan
 * 10/25/2025
 */

@NoArgsConstructor
public final class SwiftFieldUtils {

    private static final Pattern BIC_PATTERN = Pattern.compile("\\b([A-Z0-9]{8,11})\\b");

    public static String extractName(String fieldValue) {
        if (fieldValue == null || fieldValue.isBlank()) return null;

        // Split by newlines or carriage returns
        String[] lines = fieldValue.split("\\r?\\n");
        if (lines.length == 0) return null;

        // If first line starts with slash, skip to next line for name
        if (lines[0].startsWith("/")) {
            return lines.length > 1 ? lines[1].trim() : null;
        }

        return lines[0].trim();
    }

    public static String extractAccount(String fieldValue) {
        if (fieldValue == null || fieldValue.isBlank()) return null;

        // Split by newlines or carriage returns
        String[] lines = fieldValue.split("\\r?\\n");
        if (lines.length == 0) return null;

        String firstLine = lines[0].trim();
        if (firstLine.startsWith("/")) {
            return firstLine.substring(1).trim(); // remove leading "/"
        }

        return null; // no account line
    }

    public static BigDecimal extractAmount(String field32A) {
        if (field32A == null || field32A.length() < 9) return BigDecimal.ZERO;
        try {
            return new BigDecimal(field32A.substring(9).replace(',', '.'));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static String extractCurrency(String field32A, String fallback) {
        if (field32A != null && field32A.length() >= 9) return field32A.substring(6, 9);
        return fallback;
    }

    public static String extractDate(String field32A) {
        if (field32A != null && field32A.length() >= 6) {
            String d = field32A.substring(0, 6);
            return "20" + d.substring(0, 2) + "-" + d.substring(2, 4) + "-" + d.substring(4, 6);
        }
        return LocalDate.now().toString();
    }

    public static String extractBic(String raw) {
        if (raw == null) return null;
        String s = raw.replaceAll("[\\r\\n\\s]+", " ").trim();
        Matcher m = BIC_PATTERN.matcher(s);
        return m.find() ? m.group(1) : null;
    }

    public static String extractBankName(String raw) {
        if (raw == null) return null;
        for (String line : raw.replace("\r", "").split("\n")) {
            String s = line.trim();
            if (!s.isEmpty() && !s.matches("^[A-Z0-9]{8,11}$")) return s;
        }
        return null;
    }

    // --- pacs.008 builder (typed) ---
    public static BranchAndFinancialInstitutionIdentification buildAgent008(String bic, String name, String fallback) {
        FinancialInstitutionIdentification fin = new FinancialInstitutionIdentification(
                bic,
                (bic == null && name != null) ? name : fallback
        );
        return new BranchAndFinancialInstitutionIdentification(fin);
    }

    // --- pacs.009 builder (typed) ---
    public static com.mok.finmsg.mixar.model.mx.pacs009.BranchAndFinancialInstitutionIdentification buildAgent009(
            String bicOrRaw) {

        String bic = extractBic(bicOrRaw);
        com.mok.finmsg.mixar.model.mx.pacs009.FinancialInstitutionIdentification fin =
                com.mok.finmsg.mixar.model.mx.pacs009.FinancialInstitutionIdentification.builder()
                        .bicfi(bic)
                        .build();
        return com.mok.finmsg.mixar.model.mx.pacs009.BranchAndFinancialInstitutionIdentification.builder()
                .finInstnId(fin)
                .build();
    }

    public static Party buildPartyFromCov(String bicOrName) {
        Party p = new Party();
        if (bicOrName == null) return p;
        p.setName(bicOrName);
        return p;
    }

    public static RemittanceInformation buildRemittanceInfo(String field70) {
        if (field70 == null) return null;

        return RemittanceInformation.builder()
                .ustrd(field70) // just pass the string itself
                .build();
    }

    public static com.mok.finmsg.mixar.model.mx.pacs009.BranchAndFinancialInstitutionIdentification buildDbtrFrom50(String field50A, String field50K) {
        String bic = (field50A != null && !field50A.isBlank()) ? field50A.trim() : null;
        if (bic == null) return null;

        com.mok.finmsg.mixar.model.mx.pacs009.FinancialInstitutionIdentification finInstId = com.mok.finmsg.mixar.model.mx.pacs009.FinancialInstitutionIdentification.builder()
                .bicfi(bic)
                .build();

        return com.mok.finmsg.mixar.model.mx.pacs009.BranchAndFinancialInstitutionIdentification.builder()
                .finInstnId(finInstId)
                .build();
    }


    public static com.mok.finmsg.mixar.model.mx.pacs009.BranchAndFinancialInstitutionIdentification buildCdtrFrom59(String field59) {
        if (field59 == null || field59.isBlank()) return null;

        // For MVP, assume no BIC can be extracted
        com.mok.finmsg.mixar.model.mx.pacs009.FinancialInstitutionIdentification finInstId = com.mok.finmsg.mixar.model.mx.pacs009.FinancialInstitutionIdentification.builder()
                .bicfi(null) // leave empty for now
                .build();

        return com.mok.finmsg.mixar.model.mx.pacs009.BranchAndFinancialInstitutionIdentification.builder()
                .finInstnId(finInstId)
                .build();
    }

}

