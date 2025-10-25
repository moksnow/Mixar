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
 * 10/23/2025
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Party {

    @XmlElement(name = "Nm")
    private String name; // optional for a customer

    @XmlElement(name = "Id")
    private PartyIdentificationId id; // optional for FI identification
}
