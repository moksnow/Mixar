package com.mok.finmsg.mixar.model.mx.pacs008;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author m.khandan
 * 10/24/2025
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
        "chrgBr",
        "initgPty",
        "dbtr",
        "dbtrAcct",
        "dbtrAgt",
        "dbtrAgtAcct",
        "cdtrAgt",
        "cdtr",
        "cdtrAcct",
        "ultmtDbtr",
        "instrForCdtrAgt",
        "instrForNxtAgt",
        "purp",
        "rgltryRptg",
        "tax",
        "rltdRmtInf",
        "rmtInf",
        "splmtryData"
})
public class CreditTransferTransaction {

    @XmlElement(name = "PmtId", required = true)
    private PaymentIdentification pmtId;

    @XmlElement(name = "IntrBkSttlmAmt", required = true)
    private AmountWithCurrency intrBkSttlmAmt;

    @XmlElement(name = "IntrBkSttlmDt")
    private String intrBkSttlmDt;

    @XmlElement(name = "ChrgBr")
    private String chrgBr;

    @XmlElement(name = "Dbtr")
    private Party dbtr;

    @XmlElement(name = "DbtrAcct")
    private Account dbtrAcct;

    @XmlElement(name = "DbtrAgt")
    private BranchAndFinancialInstitutionIdentification dbtrAgt;

    @XmlElement(name = "DbtrAgtAcct")
    private Account dbtrAgtAcct; // optional

    @XmlElement(name = "CdtrAgt")
    private BranchAndFinancialInstitutionIdentification cdtrAgt;

    @XmlElement(name = "Cdtr")
    private Party cdtr;

    @XmlElement(name = "CdtrAcct")
    private Account cdtrAcct;

    @XmlElement(name = "UltmtDbtr")
    private Party ultmtDbtr; // optional

    @XmlElement(name = "InstrForCdtrAgt")
    private Object instrForCdtrAgt; // optional

    @XmlElement(name = "InstrForNxtAgt")
    private Object instrForNxtAgt; // optional

    @XmlElement(name = "Purp")
    private Object purp; // optional

    @XmlElement(name = "RgltryRptg")
    private Object rgltryRptg; // optional

    @XmlElement(name = "Tax")
    private Object tax; // optional

    @XmlElement(name = "RltdRmtInf")
    private Object rltdRmtInf; // optional

    @XmlElement(name = "RmtInf")
    private Object rmtInf; // optional

    @XmlElement(name = "SplmtryData")
    private Object splmtryData; // optional

    @XmlElement(name = "InitgPty")
    private Party initgPty;
}
