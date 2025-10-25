package com.mok.finmsg.mixar.service.parser;

import com.mok.finmsg.mixar.model.mt.MT103;
import com.mok.finmsg.mixar.model.mt.MT202;
import com.mok.finmsg.mixar.model.mt.MtMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
  @author m.khandan
 * 10/23/2025
 */

/**
 * Parses raw SWIFT MT message strings into MT message objects.
 * Currently supports MT103 and MT202.
 */
@Service
public class MtParserService {

    public MtMessage parse(String rawMessage) {
        if (rawMessage == null || rawMessage.isEmpty()) {
            throw new IllegalArgumentException("Raw message cannot be null or empty");
        }

        // Extract all tags into a map
        Map<String, String> tagMap = extractAllTags(rawMessage);

        // Defensive: if no tags found, probably invalid input
        if (tagMap.isEmpty()) {
            throw new IllegalArgumentException("No valid SWIFT tags found in message");
        }

        // Detect message type based on distinctive field patterns
        if (isMt103(tagMap)) {
            return parseMt103(tagMap);
        } else if (isMt202(tagMap)) {
            return parseMt202(tagMap);
        } else {
            throw new IllegalArgumentException("Unsupported or unrecognized MT message type");
        }
    }

    private boolean isMt103(Map<String, String> tags) {
        // MT103 messages always have :32A: (value date + amount) and :50: (ordering customer)
        return tags.containsKey("32A") &&
                (tags.containsKey("50") || tags.containsKey("50A") || tags.containsKey("50K") || tags.containsKey("50F")) &&
                (tags.containsKey("59") || tags.containsKey("59A") || tags.containsKey("59F"));
    }

    private boolean isMt202(Map<String, String> tags) {
        // MT202 typically has :21:, :32A:, and :58A:, may include :52A:, :53A:, :56A:, :57A:
        return tags.containsKey("21") && tags.containsKey("32A") && tags.containsKey("58A");
    }

    private MT103 parseMt103(Map<String, String> tagMap) {
        MT103 mt103 = new MT103();
        mt103.setMessageType("103");

        mt103.setField20(tagMap.get("20"));
        mt103.setField32A(tagMap.get("32A"));
        // Support all common variations
        mt103.setField50(tagMap.getOrDefault("50", tagMap.getOrDefault("50A", tagMap.getOrDefault("50K", tagMap.get("50F")))));
        mt103.setField59(tagMap.getOrDefault("59", tagMap.getOrDefault("59A", tagMap.get("59F"))));
        mt103.setField52A(tagMap.getOrDefault("52A", tagMap.get("52D"))); // Ordering institution
        mt103.setField57A(tagMap.getOrDefault("57A", tagMap.get("57D"))); // Account with institution


        return mt103;
    }

    private MT202 parseMt202(Map<String, String> tagMap) {
        MT202 mt202 = new MT202();
        mt202.setMessageType("202");

        mt202.setField20(tagMap.get("20"));
        mt202.setField21(tagMap.get("21"));
        mt202.setField32A(tagMap.get("32A"));
        mt202.setField52A(tagMap.get("52A"));
        mt202.setField53A(tagMap.get("53A"));
        mt202.setField56A(tagMap.get("56A"));
        mt202.setField57A(tagMap.get("57A"));
        mt202.setField58A(tagMap.get("58A"));
        mt202.setField72(tagMap.get("72"));

        return mt202;
    }

    private Map<String, String> extractAllTags(String raw) {
        Map<String, String> tagMap = new HashMap<>();

        // Regex to find :tag:content, multiline safe
        Pattern pattern = Pattern.compile(":(\\d{2,3}[A-Z]?):([\\s\\S]*?)(?=:\\d{2,3}[A-Z]?:|$)");
        Matcher matcher = pattern.matcher(raw);

        while (matcher.find()) {
            String tag = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            tagMap.put(tag, value);
        }

        return tagMap;
    }
}


