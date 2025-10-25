package com.mok.finmsg.mixar.model.mx.pacs009;

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
public class FinancialInstitutionIdentification {

    @XmlElement(name = "BICFI")
    private String bicfi;

    // Remove Name mapping to comply with schema
    // @XmlElement(name = "Nm")
    // private String name;

    @XmlElement(name = "Othr")
    private OtherIdentification othr; // optional fallback
}

