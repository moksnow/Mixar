package com.mok.finmsg.mixar.model.mx.pacs009;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author M_Khandan
 * Date: 10/25/2025
 * Time: 4:48 PM
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchIdentification {
    @XmlElement(name = "Id")
    private String id;
}
