package com.berryworks.edireader.hl7;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import com.berryworks.edireader.EDISyntaxException;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

public class HL7ReaderTest {

    public static final String HL7_SAMPLE = """
            MSH|^~\\&|HCM|TDS|PA000000|IMAG|200301131347|PACS ORDER MESSAGE|ORM^O01|12345|P|2.2||
            PID|||06009037|0100092582^0|CONSORT^WEB^TEST^^||19140122|M||||||||||060090376401||||
            PV1|||ACCM^AC33||||||||||||||||||||||||||||||||||||10
            ORC|NW|9013-0127|578||IP||||200301131347|PACS ORDER MESSAGE
            OBR||73.01|578|20853^CT-ANGIO CHEST W AND/OR W/O CONTRAST||||||||||||1030^CALVERT^THOMAS^J^MD^|||MAINCT||||||||200301131347||||||||
            MSH|^~\\&|HCM|TDS|PA000000|IMAG|200301131348|ANCILLARY INITIATED ORDERS|ORM^O01|23456|P|2.2||
            PID|||06009037|0100464727^0|CONSORT^WEB^TEST^^||19140122|M||||||||||060090376401||||
            PV1|||MRI||||||||||||||||||||||||||||||||||||10
            ORC|NW|W013-0047|579||IP||||200301131348|ANCILLARY INITIATED ORDERS
            OBR||1.01|579|11916^MR EXTREMITY SHOULDER||||||||||||0432^PHILLIPS^MD,^R^MACON^|||MAINMR||||||||200301131348||||ROTATOR CUFF TEAR|||||
            MSH|^~\\&|HCM|TDS|Broker|PACS|20030710151849||ORM^O01|34567|P|2.2||
            PID|||06009037||CONSORT^WEB^TEST^^|||||||||||||060090376401|
            ORC|SC||578||CM||||20030710
            OBR|||578|
            MSH|^~\\&|HCM|TDS|Broker|PACS|20030710151849||ORM^O01|45678|P|2.2||
            PID|||06009037||CONSORT^WEB^TEST^^|||||||||||||060090376401|
            ORC|SC||579||CM||||20030710
            OBR|||579|
            MSH|^~\\&|HCM|TDS|PA000000|IMAG|200301131348|ANCILLARY INITIATED ORDERS|ORM^O01|56789|P|2.2||
            PID|||06009037|0100451210^0|CONSORT^WEB^TEST^^||19140122|M||||||||||060090376401||||
            PV1|||HICMM||||||||||||||||||||||||||||||||||||10
            ORC|CA|8013-0083|580||||||200301131348|ANCILLARY INITIATED ORDERS
            OBR||4.00|580|10259^US BREAST LIMITED||||||||||||^|||HICUS||||||||200301131348|||||||||
            """;

    @Test
    public void canParseHL7() throws EDISyntaxException, IOException {
        EDIReader ediReader = EDIReaderFactory.createEDIReader(new StringReader(HL7_SAMPLE));
    }
}
