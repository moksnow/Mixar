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
 * 10/23/2025
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Party {
    @XmlElement(name = "Nm")
    private String name;

    @XmlElement(name = "Acct")
    private String account;

    @XmlElement(name = "Adr")
    private String address;

    @XmlElement(name = "BIC")
    private String bic;
}
