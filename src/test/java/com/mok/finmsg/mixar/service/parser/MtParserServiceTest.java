package com.mok.finmsg.mixar.service.parser;

import com.mok.finmsg.mixar.model.mt.MT103;
import com.mok.finmsg.mixar.model.mt.MT202;
import com.mok.finmsg.mixar.model.mt.MtMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author m.khandan
 * 10/27/2025
 */
class MtParserServiceTest {

    private final MtParserService parserService = new MtParserService();

    @Test
    void parse_validMt103_shouldReturnMT103Object() {
        String rawMessage = """
                :20:TRX12345
                :32A:251025EUR1000,00
                :50:JOHN DOE
                :59:JANE DOE
                """;

        MtMessage message = parserService.parse(rawMessage);

        assertTrue(message instanceof MT103);
        MT103 mt103 = (MT103) message;

        assertEquals("103", mt103.getMessageType());
        assertEquals("TRX12345", mt103.getField20());
        assertEquals("251025EUR1000,00", mt103.getField32A());
        assertEquals("JOHN DOE", mt103.getField50());
        assertEquals("JANE DOE", mt103.getField59());
    }

    @Test
    void parse_validMt202_shouldReturnMT202Object() {
        String rawMessage = """
                :20:TRX20251025XYZ
                :21:REF12345
                :32A:251025USD15000,00
                :58A:NATADE21XXX
                """;

        MtMessage message = parserService.parse(rawMessage);

        assertTrue(message instanceof MT202);
        MT202 mt202 = (MT202) message;

        assertEquals("202", mt202.getMessageType());
        assertEquals("TRX20251025XYZ", mt202.getField20());
        assertEquals("REF12345", mt202.getField21());
        assertEquals("251025USD15000,00", mt202.getField32A());
        assertEquals("NATADE21XXX", mt202.getField58A());
    }

    @Test
    void parse_nullInput_shouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> parserService.parse(null));

        assertEquals("Raw message cannot be null or empty", ex.getMessage());
    }

    @Test
    void parse_emptyInput_shouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> parserService.parse(""));

        assertEquals("Raw message cannot be null or empty", ex.getMessage());
    }

    @Test
    void parse_invalidTags_shouldThrowException() {
        String raw = "this is not a SWIFT message";
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> parserService.parse(raw));

        assertEquals("No valid SWIFT tags found in message", ex.getMessage());
    }

    @Test
    void parse_unknownType_shouldThrowException() {
        // Missing :50:/59: for MT103 and :58A: for MT202
        String raw = """
                :20:ABC123
                :32A:250101EUR500,00
                """;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> parserService.parse(raw));

        assertEquals("Unsupported or unrecognized MT message type", ex.getMessage());
    }
}
