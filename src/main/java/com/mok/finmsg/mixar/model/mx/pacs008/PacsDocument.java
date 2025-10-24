package com.mok.finmsg.mixar.model.mx.pacs008;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author m.khandan
 * 10/23/2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "Document")
@XmlAccessorType(XmlAccessType.FIELD)
public class PacsDocument {
    @XmlElement(name = "FIToFICstmrCdtTrf")
    private FIToFICstmrCdtTrf fiToFICstmrCdtTrf;
}
