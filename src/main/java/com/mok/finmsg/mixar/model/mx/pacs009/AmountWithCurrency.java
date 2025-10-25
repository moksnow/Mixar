package com.mok.finmsg.mixar.model.mx.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author m.khandan
 * 10/24/2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActiveOrHistoricCurrencyAndAmount")
public class AmountWithCurrency {
    @XmlValue
    private BigDecimal value;

    @XmlAttribute(name = "Ccy", required = true)
    private String currency;

}
