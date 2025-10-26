package com.mok.finmsg.mixar.controller;

import com.mok.finmsg.mixar.service.MtMxConversionService;
import jakarta.xml.bind.JAXBException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author m.khandan
 * 10/23/2025
 */
@Slf4j
@RestController
@RequestMapping("/api/mt-mx")
@AllArgsConstructor
public class MtMxController {

    private final MtMxConversionService mtMxConversionService;


    /**
     * @param mtMessage Raw MT message string in request body
     * @return MX XML string or error message
     */
    @PostMapping("/convert")
    public ResponseEntity<?> convertMtToMx(@RequestBody String mtMessage) {
        try {
            String xml = mtMxConversionService.convertAndValidate(mtMessage);

            return ResponseEntity.ok(xml);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parsing error: " + e.getMessage());
        } catch (JAXBException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Serialization error: " + e.getMessage());
        } catch (SAXException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            log.error("e: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }
}

