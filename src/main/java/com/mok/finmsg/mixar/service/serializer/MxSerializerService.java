package com.mok.finmsg.mixar.service.serializer;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

/**
 * @author m.khandan
 * 10/23/2025
 * Serializes MX (ISO 20022) objects into XML strings.
 */
@Service
public class MxSerializerService {

    /**
     * Converts a PacsDocument object into a formatted XML string.
     *
     * @param document PacsDocument (MX message)
     * @return XML string
     * @throws JAXBException if serialization fails
     */
    public String serialize(Object document) throws JAXBException {
        if (document == null) {
            throw new IllegalArgumentException("MX document cannot be null");
        }

        // Determine the correct class for JAXBContext
        Class<?> docClass = document.getClass();

        JAXBContext context = JAXBContext.newInstance(docClass);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter writer = new StringWriter();
        marshaller.marshal(document, writer);

        return writer.toString();
    }

}

