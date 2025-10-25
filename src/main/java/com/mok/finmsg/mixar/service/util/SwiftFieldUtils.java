package com.mok.finmsg.mixar.service.util;

import com.mok.finmsg.mixar.model.mx.pacs008.BranchAndFinancialInstitutionIdentification;
import com.mok.finmsg.mixar.model.mx.pacs008.FinancialInstitutionIdentification;
import com.mok.finmsg.mixar.model.mx.pacs009.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author m.khandan
 * 10/25/2025
 */
public final class SwiftFieldUtils {

    private SwiftFieldUtils() {}

    private static final Pattern BIC_PATTERN = Pattern.compile("\\b([A-Z0-9]{8,11})\\b");

    // --- parsing helpers ---

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
                bic != null ? bic : null,
                (bic == null && name != null) ? name : fallback
        );
        return new BranchAndFinancialInstitutionIdentification(fin);
    }

    // --- pacs.009 builder (typed) ---
    public static com.mok.finmsg.mixar.model.mx.pacs009.BranchAndFinancialInstitutionIdentification buildAgent009(
            String bicOrRaw, String fallback) {

        String bic = extractBic(bicOrRaw);
        com.mok.finmsg.mixar.model.mx.pacs009.FinancialInstitutionIdentification fin =
                com.mok.finmsg.mixar.model.mx.pacs009.FinancialInstitutionIdentification.builder()
                        .bicfi(bic)
                        .build();
        return com.mok.finmsg.mixar.model.mx.pacs009.BranchAndFinancialInstitutionIdentification.builder()
                .finInstnId(fin)
                .build();
    }
}

