package com.mok.finmsg.mixar.service.validator;

import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/*
  @author m.khandan
 * 10/23/2025
 */

/**
 * Validates ISO 20022 MX XML messages against XSD schema.
 */
@Service
public class MxSchemaValidator {

    /**
     * Validates the given XML string against the XSD.
     *
     * @param xml XML string
     * @throws SAXException if XML is invalid
     * @throws IOException  if I/O error occurs
     */
    public void validate(String xml, String mtType) throws SAXException, IOException {
        if (xml == null || xml.isEmpty()) {
            throw new IllegalArgumentException("XML cannot be null or empty");
        }

        // Determine XSD file based on MT type
        String xsdFile = switch (mtType) {
            case "103" -> "src/main/resources/schemas/pacs.008.001.12.xsd";
            case "202" -> "src/main/resources/schemas/pacs.009.001.11.xsd";
            default -> throw new IllegalArgumentException("Unsupported MT type for validation");
        };

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new File(xsdFile));

        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(new StringReader(xml)));
    }
}
