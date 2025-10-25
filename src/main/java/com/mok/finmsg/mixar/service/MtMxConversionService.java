package com.mok.finmsg.mixar.service;

import com.mok.finmsg.mixar.model.mt.MtMessage;
import com.mok.finmsg.mixar.service.mapper.MtMapperService;
import com.mok.finmsg.mixar.service.parser.MtParserService;
import com.mok.finmsg.mixar.service.serializer.MxSerializerService;
import com.mok.finmsg.mixar.service.validator.MxSchemaValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author m.khandan
 * 10/24/2025
 */
@Service
@AllArgsConstructor
public class MtMxConversionService {
    private final MtParserService parserService;
    private final MtMapperService mapperService;
    private final MxSerializerService serializerService;
    private final MxSchemaValidator validatorService;


    public String convertAndValidate(String mtMessage) throws Exception {
        // 1. Parse MT message
        MtMessage mt = parserService.parse(mtMessage);

        // 2. Map MT → MX
        Object mx = mapperService.map(mt);

        // 3. Serialize MX → XML
        String xml;
        if (mx instanceof com.mok.finmsg.mixar.model.mx.pacs008.PacsDocument pacs008) {
            xml = serializerService.serialize(pacs008);
            System.out.println(xml); // check the exact order under <GrpHdr>

            validatorService.validate(xml, "103");
        } else if (mx instanceof com.mok.finmsg.mixar.model.mx.pacs009.PacsDocument pacs009) {
            xml = serializerService.serialize(pacs009);
            System.out.println(xml); // check the exact order under <GrpHdr>
            validatorService.validate(xml, "202");
        } else {
            throw new IllegalArgumentException("Unsupported MX type: " + mx.getClass().getSimpleName());
        }

        System.out.println("MT type: " + mt.getMessageType());
        System.out.println("Generated XML: " + xml);
        return xml;
    }
}
