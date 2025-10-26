package com.mok.finmsg.mixar.model.mx.pacs009;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author M_Khandan
 * Date: 10/25/2025
 * Time: 4:09 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "pmtId",
        "intrBkSttlmAmt",
        "intrBkSttlmDt",
        "sttlmPrty",
        "sttlmTmIndctn",
        "instgAgt",
        "instdAgt",
        "intrmyAgt1",
        "intrmyAgt1Acct",
        "intrmyAgt2",
        "intrmyAgt2Acct",
        "intrmyAgt3",
        "intrmyAgt3Acct",
        "ultmtDbtr",
        "dbtr",
        "dbtrAcct",
        "dbtrAgt",
        "dbtrAgtAcct",
        "cdtrAgt",
        "cdtrAgtAcct",
        "cdtr",
        "purp",
        "rgltryRptg",
        "splmtryData"
})
public class CreditTransferTransactionFI {
    @XmlElement(name = "PmtId", required = true)
    private PaymentIdentification pmtId;

    @XmlElement(name = "IntrBkSttlmAmt", required = true)
    private AmountWithCurrency intrBkSttlmAmt;

    @XmlElement(name = "IntrBkSttlmDt")
    private String intrBkSttlmDt;

    @XmlElement(name = "SttlmPrty")
    private String sttlmPrty; // optional

    @XmlElement(name = "SttlmTmIndctn")
    private String sttlmTmIndctn; // optional

    @XmlElement(name = "InstgAgt")
    private BranchAndFinancialInstitutionIdentification instgAgt;

    @XmlElement(name = "InstdAgt")
    private BranchAndFinancialInstitutionIdentification instdAgt;

    @XmlElement(name = "IntrmyAgt1")
    private BranchAndFinancialInstitutionIdentification intrmyAgt1;

    @XmlElement(name = "IntrmyAgt1Acct")
    private Account intrmyAgt1Acct;

    @XmlElement(name = "IntrmyAgt2")
    private BranchAndFinancialInstitutionIdentification intrmyAgt2;

    @XmlElement(name = "IntrmyAgt2Acct")
    private Account intrmyAgt2Acct;

    @XmlElement(name = "IntrmyAgt3")
    private BranchAndFinancialInstitutionIdentification intrmyAgt3;

    @XmlElement(name = "IntrmyAgt3Acct")
    private Account intrmyAgt3Acct;

    @XmlElement(name = "UltmtDbtr")
    private BranchAndFinancialInstitutionIdentification ultmtDbtr;

    @XmlElement(name = "Dbtr")
    private BranchAndFinancialInstitutionIdentification dbtr;

    @XmlElement(name = "DbtrAcct")
    private Account dbtrAcct;

    @XmlElement(name = "DbtrAgt", required = true)
    private BranchAndFinancialInstitutionIdentification dbtrAgt;

    @XmlElement(name = "DbtrAgtAcct")
    private Account dbtrAgtAcct; // optional

    @XmlElement(name = "CdtrAgt", required = true)
    private BranchAndFinancialInstitutionIdentification cdtrAgt;

    @XmlElement(name = "CdtrAgtAcct")
    private Account cdtrAgtAcct;

    @XmlElement(name = "Cdtr")
    private BranchAndFinancialInstitutionIdentification cdtr;

    @XmlElement(name = "Purp")
    private Object purp; // optional

    @XmlElement(name = "RgltryRptg")
    private Object rgltryRptg; // optional

    @XmlElement(name = "SplmtryData")
    private Object splmtryData; // optional
}
