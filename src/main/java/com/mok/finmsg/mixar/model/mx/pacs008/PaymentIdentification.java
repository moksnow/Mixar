package com.mok.finmsg.mixar.model.mx.pacs008;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
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
public class PaymentIdentification {

    @XmlElement(name = "InstrId")
    private String instrId; // optional, can be null

    @XmlElement(name = "EndToEndId", required = true)
    private String endToEndId; // usually maps from MT103 :20: field
}
