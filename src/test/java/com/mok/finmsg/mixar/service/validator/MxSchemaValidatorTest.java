package com.mok.finmsg.mixar.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author m.khandan
 * 10/27/2025
 */
class MxSchemaValidatorTest {

    private MxSchemaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MxSchemaValidator();
    }

    @Test
    void validate_nullXml_shouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(null, "103"));
        assertEquals("XML cannot be null or empty", ex.getMessage());
    }

    @Test
    void validate_emptyXml_shouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validate("", "202"));
        assertEquals("XML cannot be null or empty", ex.getMessage());
    }

    @Test
    void validate_unsupportedMtType_shouldThrowException() {
        String xml = "<Document></Document>";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(xml, "999"));

        assertEquals("Unsupported MT type for validation", ex.getMessage());
    }
}
