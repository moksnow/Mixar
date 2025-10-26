package com.mok.finmsg.mixar.model.mx.pacs009;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author M_Khandan
 * Date: 10/25/2025
 * Time: 4:10 PM
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"grpHdr", "cdtTrfTxInf"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FICdtTrf {
    @XmlElement(name = "GrpHdr", required = true)
    private GroupHeader grpHdr;

    @XmlElement(name = "CdtTrfTxInf", required = true)
    private List<CreditTransferTransactionFI> cdtTrfTxInf;
}
