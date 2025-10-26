package com.mok.finmsg.mixar.model.mx.pacs008;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author m.khandan
 * 10/24/2025
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class FIToFICstmrCdtTrf {
    @XmlElement(name = "GrpHdr")
    private GroupHeader grpHdr;

    @XmlElement(name = "CdtTrfTxInf")
    private List<CreditTransferTransaction> cdtTrfTxInf;
}
