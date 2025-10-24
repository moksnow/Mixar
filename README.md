# Mixar

**Mixar** is a Spring Boot application that converts **SWIFT MT messages (MT103 & MT202)** into **ISO 20022 MX messages (pacs.008 & pacs.009)**. It automatically detects the MT type, maps fields to the corresponding MX structure, serializes to XML, and validates the result against official ISO 20022 XSDs.  

---

## Features

- Convert **MT103 → pacs.008.001.12**  
- Convert **MT202 → pacs.009.001.11**  
- Automatic detection of MT type  
- XML serialization using JAXB  
- Validation against ISO 20022 XSD (Payments Clearing and Settlement V13) 
- Handles mandatory and optional fields correctly  
- REST API endpoint for easy integration  

---

## Supported Standards

| SWIFT MT | ISO 20022 MX | Description |
|----------|---------------|-------------|
| MT103    | pacs.008.001.12 | Customer Credit Transfer | Payments Clearing and Settlement V13 |
| MT202    | pacs.009.001.11 | Financial Institution Transfer |  ...  |

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
        │
        ├─ Step 2: Detect MT type
        │       ├─ MT103 → pacs.008.001.12
        │       └─ MT202 → pacs.009.001.11
        │
        ├─ Step 3: Map MT → MX → PacsDocument object
        │
        ├─ Step 4: Serialize MX → XML
        │
        ├─ Step 5: Validate XML against ISO 20022 XSD
        │
        └─ Step 6: Return validated XML to controller
        │
        ▼
[HTTP Response: 200 OK or 400/500 Error]
```

---

### MT → MX Mapping Highlights

- **MT103 fields** → **pacs.008 elements**:
  - `Field 20` → `PmtId/InstrId & EndToEndId`
  - `Field 32A` → `IntrBkSttlmAmt` and `IntrBkSttlmDt`
  - `Field 50` → `Dbtr & DbtrAcct`
  - `Field 59` → `Cdtr & CdtrAcct`
  - `Field 52A` → `InstgAgt`
  - `Field 57A` → `InstdAgt`
- Optional elements like `InitgPty`, `UltmtDbtr`, `RmtInf`, `SplmtryData` are handled as nullable
- Strict **element order** enforced via JAXB `@XmlType(propOrder)` for XSD compliance

- **MT202 fields** → **pacs.009 elements** mapped similarly for interbank transfers

---

## REST API

**POST** `/convert`  
**Content-Type:** `application/json`  

**Request Body Example:**

**Response:**

- **200 OK** → Returns validated MX XML  
- **400 Bad Request** → Parsing or validation error  
- **500 Internal Server Error** → Unexpected errors  


**Example Request XML:**
You can use this **minimal MT103 message** to test your Mixar API quickly:

```
:20:TRX98765
:32A:251025EUR2500,00
:50:Alice Sender
:59:Bob Receiver
```

**Test with curl:**

```bash
curl -X POST http://localhost:8080/api/mt-mx/convert \
     -H "Content-Type: text/plain" \
     -d ":20:TRX98765
:32A:251025EUR2500,00
:50:Alice Sender
:59:Bob Receiver"
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

1. Clone the repository:

```bash
git clone https://github.com/moksnow/Mixar.git
cd Mixar
```

2. Build with Maven:

```bash
mvn clean install
```

3. Run the application:

```bash
mvn spring-boot:run
```

The REST API will be available at `http://localhost:8080/convert`.

---

## Technologies

- Java 21  
- Spring Boot 3.x  
- JAXB for XML binding  
- ISO 20022 XSD validation  
- Lombok for model classes  
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