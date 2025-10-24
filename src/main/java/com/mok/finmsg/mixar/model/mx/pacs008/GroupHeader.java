package com.mok.finmsg.mixar.model.mx.pacs008;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author m.khandan
 * 10/23/2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "msgId",
        "creDtTm",
        "nbOfTxs",
        "ctrlSum",
        "ttlIntrBkSttlmAmt",
        "intrBkSttlmDt",
        "sttlmInf",
        "pmtTpInf",
        "instgAgt",
        "instdAgt",
        "btchBookg"
})
public class GroupHeader {
    @XmlElement(name = "MsgId")
    private String msgId;

    @XmlElement(name = "CreDtTm")
    private String creDtTm;

    @XmlElement(name = "NbOfTxs")
    private String nbOfTxs;

    @XmlElement(name = "CtrlSum")
    private BigDecimal ctrlSum;

    @XmlElement(name = "TtlIntrBkSttlmAmt")
    private AmountWithCurrency ttlIntrBkSttlmAmt;

    @XmlElement(name = "IntrBkSttlmDt")
    private String intrBkSttlmDt;

    @XmlElement(name = "SttlmInf")
    private SettlementInfo sttlmInf;

    @XmlElement(name = "PmtTpInf")
    private PaymentTypeInformation pmtTpInf;

    @XmlElement(name = "InstgAgt")
    private BranchAndFinancialInstitutionIdentification instgAgt;

    @XmlElement(name = "InstdAgt")
    private BranchAndFinancialInstitutionIdentification instdAgt;

    @XmlElement(name = "BtchBookg")
    private Boolean btchBookg;
}
