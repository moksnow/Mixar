package com.mok.finmsg.mixar.model.mt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author m.khandan
 * 10/23/2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MtMessage {
    private String messageType; // e.g., 103, 202
    private Map<String, String> tags = new HashMap<>(); // tag → value
}
