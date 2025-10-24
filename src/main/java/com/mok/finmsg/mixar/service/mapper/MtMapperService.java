package com.mok.finmsg.mixar.service.mapper;

import com.mok.finmsg.mixar.model.mt.MT103;
import com.mok.finmsg.mixar.model.mt.MT202;
import com.mok.finmsg.mixar.model.mt.MtMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Central mapping service: routes MT messages to the correct mapper.
 */
@Service
public class MtMapperService {

    private final Mt103ToPacs008Mapper mt103Mapper;
    private final Mt202ToPacs009Mapper mt202Mapper;

    @Autowired
    public MtMapperService(Mt103ToPacs008Mapper mt103Mapper,
                           Mt202ToPacs009Mapper mt202Mapper) {
        this.mt103Mapper = mt103Mapper;
        this.mt202Mapper = mt202Mapper;
    }

    /**
     * Maps an MT message to the corresponding MX document (pacs.008 or pacs.009)
     */
    public Object map(MtMessage message) {
        if (message instanceof MT103 mt103) {
            return mt103Mapper.convert(mt103); // returns pacs008.PacsDocument
        } else if (message instanceof MT202 mt202) {
            return mt202Mapper.convert(mt202); // returns pacs009.PacsDocument
        } else {
            throw new IllegalArgumentException("Unsupported MT message type: "
                    + (message != null ? message.getClass().getSimpleName() : "null"));
        }
    }

    /**
     * Optional: convenience methods to avoid casting in services/controllers
     */
    public com.mok.finmsg.mixar.model.mx.pacs008.PacsDocument map(MT103 mt103) {
        return mt103Mapper.convert(mt103);
    }

    public com.mok.finmsg.mixar.model.mx.pacs009.PacsDocument map(MT202 mt202) {
        return mt202Mapper.convert(mt202);
    }
}

