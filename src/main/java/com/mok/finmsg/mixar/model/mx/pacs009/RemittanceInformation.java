package com.mok.finmsg.mixar.model.mx.pacs009;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author M_Khandan
 * Date: 10/25/2025
 * Time: 4:27 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemittanceInformation {
    private String ustrd; // Unstructured text
}
