![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow)
![Stars](https://img.shields.io/github/stars/moksnow/Mixar?style=social)

[//]: # (![Build]&#40;https://github.com/moksnow/Mixar/actions/workflows/maven.yml/badge.svg&#41;)

# Mixar

**Mixar** is a Spring Boot application that converts **SWIFT MT messages (MT103, MT202, MT202COV)** into **ISO 20022 MX messages (pacs.008 & pacs.009)**.  
It automatically detects the MT type, maps fields to the corresponding MX structure, serializes to XML, and validates the result against official ISO 20022 XSDs.

---

## Features

- Convert **MT103 → pacs.008.001.12**  
- Convert **MT202 → pacs.009.001.11**  
- Convert **MT202 COV → pacs.009.001.11 (cover payment)**
- Automatic detection of MT type  
- XML serialization using JAXB  
- Validation against ISO 20022 XSD (Payments Clearing and Settlement V13) 
- Handles mandatory and optional fields correctly  
- REST API endpoint for easy integration  

---

## Quick Start

### 1. Clone
```bash
git clone https://github.com/moksnow/Mixar.git
```
### 2. Run
```bash
mvn spring-boot:run
```
### 3. Test conversion
```bash
curl -X POST http://localhost:8080/api/mt-mx/convert \
-H "Content-Type: text/plain" \
-d ":20:TRX98765
:32A:251025EUR2500,00
:50:Alice
:59:Bob"
```
---

## Supported Standards

| SWIFT MT | ISO 20022 MX | Description |
|----------|---------------|-------------|
| MT103 | pacs.008.001.12 | Customer Credit Transfer |
| MT202 | pacs.009.001.11 | Financial Institution Transfer |
| MT202 COV | pacs.009.001.11 | Cover Payment between FIs |

---

## Conversion Flow

```text
[HTTP POST /convert]
        │
        ▼
   [Controller: convertMtToMx]
        │
        ▼
 [MtMxConversionService.convertAndValidate]
        │
        ├─ Step 1: Parse MT message → MtMessage object
        ├─ Step 2: Detect MT type (103, 202, 202COV)
        ├─ Step 3: Map MT → MX → PacsDocument object
        ├─ Step 4: Serialize MX → XML
        ├─ Step 5: Validate XML against ISO 20022 XSD
        └─ Step 6: Return validated XML
```

---

## MT → MX Mapping Highlights

### MT103 → pacs.008
| MT Field | MX Element |
|-----------|------------|
| :20: | PmtId/InstrId & EndToEndId |
| :32A: | IntrBkSttlmAmt & IntrBkSttlmDt |
| :50a: | Dbtr & DbtrAcct |
| :59a: | Cdtr & CdtrAcct |
| :52A: | InstgAgt |
| :57A: | InstdAgt |

### MT202 → pacs.009
| MT Field | MX Element |
|-----------|------------|
| :20: | PmtId/InstrId |
| :21: | PmtId/UETR or TxRef |
| :32A: | IntrBkSttlmAmt & IntrBkSttlmDt |
| :52A: | InstgAgt |
| :58A: | InstdAgt |

### MT202 COV → pacs.009 (Cover Payment)
Includes MT202 fields + additional customer info:

| MT202 COV Field | MX Element |
|------------------|------------|
| :50a: Ordering Customer | UltmtDbtr / Dbtr |
| :59: Beneficiary Customer | Cdtr |
| :70: Payment Details | RmtInf/Ustrd |
| :72: Sender to Receiver | SplmtryData or InstrForNxtAgt |

---

## REST API Usage

**POST** `/convert`  
**Content‑Type:** `text/plain`

### Example MT103
```
:20:TRX98765
:32A:251025EUR2500,00
:50:Alice Sender
:59:Bob Receiver
```

**Test MT103 with curl:**

```bash
curl -X POST http://localhost:8080/api/mt-mx/convert \
     -H "Content-Type: text/plain" \
     -d ":20:TRX98765
:32A:251025EUR2500,00
:50:Alice Sender
:59:Bob Receiver"
```


You can use this **minimal MT202 message** to test your Mixar API quickly:
```
{1:F01BANKBEBBAXXX0000000000}{2:I202BANKDEFFXXXXN}{4:
:20:TRX202002
:21:RELREF202
:32A:251028USD10000,00
:52A:BANKBEBBXXX
:53A:BANKDEFFXXX
:56A:BANKUS33XXX
:57A:BANKFRPPXXX
:58A:BANKGB22XXX
-}
```

**Test MT202 with curl:**

```bash
curl -X POST http://localhost:8080/api/mt-mx/convert \
      -H "Content-Type: text/plain" \
      -d "{1:F01BANKBEBBAXXX0000000000}{2:I202BANKDEFFXXXXN}{4:
:20:TRX202002
:21:RELREF202
:32A:251028USD10000,00
:52A:BANKBEBBXXX
:53A:BANKDEFFXXX
:56A:BANKUS33XXX
:57A:BANKFRPPXXX
:58A:BANKGB22XXX
-}"

```

You can use this **minimal MT202COV message** to test your Mixar API quickly:
```
{1:F01BANKBEBBAXXX0000000000}{2:I202BANKDEFFXXXXN}{4:
:20:TRX202COV001
:21:RELREF001
:32A:251028EUR10000,00
:50K:/123456789
Alice Customer
:59:/987654321
Bob Beneficiary
:52A:BANKBEBB
:53A:BANKDEFF
:56A:BANKUS33
:57A:BANKFRPP
:58A:BANKIT33
:70:Payment for invoice 12345
:72:/INS/Instructions
-}
```
**Test MT202COV with curl:**

```bash
curl -X POST http://localhost:8080/api/mt-mx/convert \
      -H "Content-Type: text/plain" \
      -d "{1:F01BANKBEBBAXXX0000000000}{2:I202BANKDEFFXXXXN}{4:
:20:TRX202COV001
:21:RELREF001
:32A:251028EUR10000,00
:50K:/123456789
Alice Customer
:59:/987654321
Bob Beneficiary
:52A:BANKBEBB
:53A:BANKDEFF
:56A:BANKUS33
:57A:BANKFRPP
:58A:BANKIT33
:70:Payment for invoice 12345
:72:/INS/Instructions
-}"

```

**Example Response XML:**

```xml
<PacsDocument>
    <FIToFICstmrCdtTrf>
        <GrpHdr>...</GrpHdr>
        <CdtTrfTxInf>
            <PmtId>...</PmtId>
            <IntrBkSttlmAmt Ccy="EUR">1000.00</IntrBkSttlmAmt>
            <Dbtr>...</Dbtr>
            <InitgPty>...</InitgPty>
            ...
        </CdtTrfTxInf>
    </FIToFICstmrCdtTrf>
</PacsDocument>
```

✅ The app will automatically detect it as **MT103**, map it to **pacs.008.001.12**, serialize the XML, and validate it against the ISO 20022 schema.

---

## Installation

```bash
git clone https://github.com/moksnow/Mixar.git
cd Mixar
mvn clean install
mvn spring-boot:run
```

Then open → `http://localhost:8080/convert`

---

## Technologies

- Java 21  
- Spring Boot 3.x  
- JAXB (XML binding)
- ISO 20022 XSD validation  
- Lombok
- Maven build system  

---

## Notes

- Ensure MT messages strictly follow the SWIFT format.  
- Element order in MX messages is critical for passing XSD validation.  
- Optional fields are supported but must follow the correct sequence in XML.  
- Currently supports **MT103 → pacs.008** and **MT202 → pacs.009** only.  

---

## License

MIT License – free to use and contribute.  