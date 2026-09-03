package com.berryworks.edireader.hl7;

import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.util.Conversion;
import org.junit.Test;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static com.berryworks.edireader.util.TestUtil.assertEqualsDisregardingSpacesAndLineSeparators;

public class HL7ReaderTest {

    @Test
    public void canParseHL7() throws EDISyntaxException, IOException, TransformerException {
        StringReader stringReader = new StringReader(HL7_SAMPLE);
        StringWriter xmlOutput = new StringWriter();
        Conversion.ediToXml(stringReader, xmlOutput, new HL7Reader());
        String expected = HL7_XML.replace("\n", "");
        String actual = xmlOutput.toString().replace("\n", "");
        assertEqualsDisregardingSpacesAndLineSeparators(expected, actual);
    }

    @Test
    public void canParse3Levels() throws EDISyntaxException, IOException, TransformerException {
        StringReader stringReader = new StringReader(HL7_3_LEVELS);
        StringWriter xmlOutput = new StringWriter();
        Conversion.ediToXml(stringReader, xmlOutput, new HL7Reader());
        String actual = xmlOutput.toString();
        assertEqualsDisregardingSpacesAndLineSeparators(HL7_XML_3_LEVELS, actual);
    }

    @Test
    public void canParse3LevelsWithRepetition() throws EDISyntaxException, IOException, TransformerException {
        StringReader stringReader = new StringReader(HL7_3_LEVELS_WITH_REPETITION);
        StringWriter xmlOutput = new StringWriter();
        Conversion.ediToXml(stringReader, xmlOutput, new HL7Reader());
        String actual = xmlOutput.toString();
        assertEqualsDisregardingSpacesAndLineSeparators(HL7_XML_3_LEVELS_WITH_REPETITION, actual);
    }

    @Test
    public void tinySample() throws TransformerException {
        StringWriter xmlOutput = new StringWriter();
        Conversion.ediToXml(new StringReader("""
                MSH|^~\\&|HCM|TDS|PA000000|IMAG|200301131347|PACS ORDER MESSAGE|ORM^O01|12345|P|2.2||
                PID|||06009037|0100092582^0|CONSORT^WEB^TEST^^||19140122|M||||||||||060090376401||||
                PV1|||ACCM^AC33||||||||||||||||||||||||||||||||||||10
                ORC|NW|901
                """), xmlOutput, new HL7Reader());
    }

    public static final String HL7_3_LEVELS = """
            MSH|^~\\&|KeyMed|Retina Services of Illinois LLC|Travercent|Retina Services of Illinois LLC|20161207082851||SIU^S12|5000683794|P|2.3.1|1|
            SCH|463733||||||^C - CONSULT|^Consult|10|min^Minutes|^^^20161207093000^20161207094000||||||||||||||^Pending|
            PID|||20084571^^^^PT|76432|Martinez^Robert^^^Mr.||19620417|M||White|4217 N Maplewood^^Chicago^IL^60618||(773) 555-0147^PRN^PH|^WPN^PH|English|U||20084571||||Not Hispanic or Latino||||||||N||||||||||Home|
            PV1|||O||||PRRS^Bennett^Michael|DEC^Carter^Laura||||||||||9|||||||||||||||||||||||A|
            AIL|||O^ ResurrectionOffice Retina Services Ill^^^^^^^O|
            AIP|||MICHAEL^Bennett^Michael T.^^^^^^&&NPI|
            """;

    public static final String HL7_3_LEVELS_WITH_REPETITION = """
            MSH|^~\\&|KeyMed|Retina Services of Illinois LLC|Travercent|Retina Services of Illinois LLC|20161207082851||SIU^S12|5000683794|P|2.3.1|1|
            SCH|463733||||||^C - CONSULT|^Consult|10|min^Minutes|^^^20161207093000^20161207094000||||||||||||||^Pending|
            PID|||20084571^^^^PT~76432^^^^PI~20084571^^^^MR~20084571^^^^AN|76432|Martinez^Robert^^^Mr.||19620417|M||White|4217 N Maplewood^^Chicago^IL^60618||(773) 555-0147^PRN^PH|^WPN^PH|English|U||20084571||||Not Hispanic or Latino||||||||N||||||||||Home|
            PV1|||O||||PRRS^Bennett^Michael|DEC^Carter^Laura||||||||||9|||||||||||||||||||||||A|
            AIL|||O^ ResurrectionOffice Retina Services Ill^^^^^^^O|
            AIP|||MICHAEL^Bennett^Michael T.^^^^^^&&NPI|
            """;

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

    public static final String HL7_XML_3_LEVELS = """
            <?xml version="1.0" encoding="UTF-8"?>
            <ediroot>
                <interchange Standard="HL7">
                    <group ApplSender="KeyMed" SendingFacility="Retina Services of Illinois LLC" ApplReceiver="Travercent"
                           ReceivingFacility="Retina Services of Illinois LLC" Date="20161207" Time="082851" Type="SIU"
                           TypeDesc="Schedule information unsolicited" Event="S12"
                           EventDesc="Notification of new appointment booking" Control="5000683794" ProcessingId="P"
                           SyntaxVersion="2.3.1" sequenceNumber="1">
                        <transaction Type="SIU" Event="S12" Control="5000683794">
                            <segment Id="SCH">
                                <element Id="SCH01">463733</element>
                                <element Id="SCH07" Composite="yes">
                                    <subelement Sequence="2">C - CONSULT</subelement>
                                </element>
                                <element Id="SCH08" Composite="yes">
                                    <subelement Sequence="2">Consult</subelement>
                                </element>
                                <element Id="SCH09">10</element>
                                <element Id="SCH10" Composite="yes">
                                    <subelement Sequence="1">min</subelement>
                                    <subelement Sequence="2">Minutes</subelement>
                                </element>
                                <element Id="SCH11" Composite="yes">
                                    <subelement Sequence="4">20161207093000</subelement>
                                    <subelement Sequence="5">20161207094000</subelement>
                                </element>
                                <element Id="SCH25" Composite="yes">
                                    <subelement Sequence="2">Pending</subelement>
                                </element>
                            </segment>
                            <segment Id="PID">
                                <element Id="PID03" Composite="yes">
                                    <subelement Sequence="1">20084571</subelement>
                                    <subelement Sequence="5">PT</subelement>
                                </element>
                                <element Id="PID04">76432</element>
                                <element Id="PID05" Composite="yes">
                                    <subelement Sequence="1">Martinez</subelement>
                                    <subelement Sequence="2">Robert</subelement>
                                    <subelement Sequence="5">Mr.</subelement>
                                </element>
                                <element Id="PID07">19620417</element>
                                <element Id="PID08">M</element>
                                <element Id="PID10">White</element>
                                <element Id="PID11" Composite="yes">
                                    <subelement Sequence="1">4217 N Maplewood</subelement>
                                    <subelement Sequence="3">Chicago</subelement>
                                    <subelement Sequence="4">IL</subelement>
                                    <subelement Sequence="5">60618</subelement>
                                </element>
                                <element Id="PID13" Composite="yes">
                                    <subelement Sequence="1">(773) 555-0147</subelement>
                                    <subelement Sequence="2">PRN</subelement>
                                    <subelement Sequence="3">PH</subelement>
                                </element>
                                <element Id="PID14" Composite="yes">
                                    <subelement Sequence="2">WPN</subelement>
                                    <subelement Sequence="3">PH</subelement>
                                </element>
                                <element Id="PID15">English</element>
                                <element Id="PID16">U</element>
                                <element Id="PID18">20084571</element>
                                <element Id="PID22">Not Hispanic or Latino</element>
                                <element Id="PID30">N</element>
                                <element Id="PID40">Home</element>
                            </segment>
                            <segment Id="PV1">
                                <element Id="PV103">O</element>
                                <element Id="PV107" Composite="yes">
                                    <subelement Sequence="1">PRRS</subelement>
                                    <subelement Sequence="2">Bennett</subelement>
                                    <subelement Sequence="3">Michael</subelement>
                                </element>
                                <element Id="PV108" Composite="yes">
                                    <subelement Sequence="1">DEC</subelement>
                                    <subelement Sequence="2">Carter</subelement>
                                    <subelement Sequence="3">Laura</subelement>
                                </element>
                                <element Id="PV118">9</element>
                                <element Id="PV141">A</element>
                            </segment>
                            <segment Id="AIL">
                                <element Id="AIL03" Composite="yes">
                                    <subelement Sequence="1">O</subelement>
                                    <subelement Sequence="2">ResurrectionOffice Retina Services Ill</subelement>
                                    <subelement Sequence="9">O</subelement>
                                </element>
                            </segment>
                            <segment Id="AIP">
                                <element Id="AIP03" Composite="yes">
                                    <subelement Sequence="1">MICHAEL</subelement>
                                    <subelement Sequence="2">Bennett</subelement>
                                    <subelement Sequence="3">Michael T.</subelement>
                                    <subelement Sequence="9" Composite="yes">
                                        <subsubelement Sequence="3">NPI</subsubelement>
                                    </subelement>
                                </element>
                            </segment>
                        </transaction>
                    </group>
                </interchange>
            </ediroot>""";

    public static final String HL7_XML_3_LEVELS_WITH_REPETITION = """
            <?xml version="1.0" encoding="UTF-8"?>
            <ediroot>
                <interchange Standard="HL7">
                    <group ApplSender="KeyMed" SendingFacility="Retina Services of Illinois LLC" ApplReceiver="Travercent"
                           ReceivingFacility="Retina Services of Illinois LLC" Date="20161207" Time="082851" Type="SIU"
                           TypeDesc="Schedule information unsolicited" Event="S12"
                           EventDesc="Notification of new appointment booking" Control="5000683794" ProcessingId="P"
                           SyntaxVersion="2.3.1" sequenceNumber="1">
                        <transaction Type="SIU" Event="S12" Control="5000683794">
                            <segment Id="SCH">
                                <element Id="SCH01">463733</element>
                                <element Id="SCH07" Composite="yes">
                                    <subelement Sequence="2">C - CONSULT</subelement>
                                </element>
                                <element Id="SCH08" Composite="yes">
                                    <subelement Sequence="2">Consult</subelement>
                                </element>
                                <element Id="SCH09">10</element>
                                <element Id="SCH10" Composite="yes">
                                    <subelement Sequence="1">min</subelement>
                                    <subelement Sequence="2">Minutes</subelement>
                                </element>
                                <element Id="SCH11" Composite="yes">
                                    <subelement Sequence="4">20161207093000</subelement>
                                    <subelement Sequence="5">20161207094000</subelement>
                                </element>
                                <element Id="SCH25" Composite="yes">
                                    <subelement Sequence="2">Pending</subelement>
                                </element>
                            </segment>
                            <segment Id="PID">
                                <element Id="PID03" Composite="yes">
                                    <subelement Sequence="1">20084571</subelement>
                                    <subelement Sequence="5">PT</subelement>
                                </element>
                                <element Id="PID03" Composite="yes">
                                    <subelement Sequence="1">76432</subelement>
                                    <subelement Sequence="5">PI</subelement>
                                </element>
                                <element Id="PID03" Composite="yes">
                                    <subelement Sequence="1">20084571</subelement>
                                    <subelement Sequence="5">MR</subelement>
                                </element>
                                <element Id="PID03" Composite="yes">
                                    <subelement Sequence="1">20084571</subelement>
                                    <subelement Sequence="5">AN</subelement>
                                </element>
                                <element Id="PID04">76432</element>
                                <element Id="PID05" Composite="yes">
                                    <subelement Sequence="1">Martinez</subelement>
                                    <subelement Sequence="2">Robert</subelement>
                                    <subelement Sequence="5">Mr.</subelement>
                                </element>
                                <element Id="PID07">19620417</element>
                                <element Id="PID08">M</element>
                                <element Id="PID10">White</element>
                                <element Id="PID11" Composite="yes">
                                    <subelement Sequence="1">4217 N Maplewood</subelement>
                                    <subelement Sequence="3">Chicago</subelement>
                                    <subelement Sequence="4">IL</subelement>
                                    <subelement Sequence="5">60618</subelement>
                                </element>
                                <element Id="PID13" Composite="yes">
                                    <subelement Sequence="1">(773) 555-0147</subelement>
                                    <subelement Sequence="2">PRN</subelement>
                                    <subelement Sequence="3">PH</subelement>
                                </element>
                                <element Id="PID14" Composite="yes">
                                    <subelement Sequence="2">WPN</subelement>
                                    <subelement Sequence="3">PH</subelement>
                                </element>
                                <element Id="PID15">English</element>
                                <element Id="PID16">U</element>
                                <element Id="PID18">20084571</element>
                                <element Id="PID22">Not Hispanic or Latino</element>
                                <element Id="PID30">N</element>
                                <element Id="PID40">Home</element>
                            </segment>
                            <segment Id="PV1">
                                <element Id="PV103">O</element>
                                <element Id="PV107" Composite="yes">
                                    <subelement Sequence="1">PRRS</subelement>
                                    <subelement Sequence="2">Bennett</subelement>
                                    <subelement Sequence="3">Michael</subelement>
                                </element>
                                <element Id="PV108" Composite="yes">
                                    <subelement Sequence="1">DEC</subelement>
                                    <subelement Sequence="2">Carter</subelement>
                                    <subelement Sequence="3">Laura</subelement>
                                </element>
                                <element Id="PV118">9</element>
                                <element Id="PV141">A</element>
                            </segment>
                            <segment Id="AIL">
                                <element Id="AIL03" Composite="yes">
                                    <subelement Sequence="1">O</subelement>
                                    <subelement Sequence="2">ResurrectionOffice Retina Services Ill</subelement>
                                    <subelement Sequence="9">O</subelement>
                                </element>
                            </segment>
                            <segment Id="AIP">
                                <element Id="AIP03" Composite="yes">
                                    <subelement Sequence="1">MICHAEL</subelement>
                                    <subelement Sequence="2">Bennett</subelement>
                                    <subelement Sequence="3">Michael T.</subelement>
                                    <subelement Sequence="9" Composite="yes">
                                        <subsubelement Sequence="3">NPI</subsubelement>
                                    </subelement>
                                </element>
                            </segment>
                        </transaction>
                    </group>
                </interchange>
            </ediroot>""";

    public static final String HL7_XML = """
            <?xml version="1.0" encoding="UTF-8"?><ediroot>
            <interchange Standard="HL7">
            <group ApplSender="HCM" SendingFacility="TDS" ApplReceiver="PA000000" ReceivingFacility="IMAG" Date="20030113" Time="1347" Security="PACS ORDER MESSAGE" Type="ORM" TypeDesc="Order message" Event="O01" EventDesc="Order message (also RDE, RDS, RGV, RAS)" Control="12345" ProcessingId="P" SyntaxVersion="2.2">
            <transaction Type="ORM" Event="O01" Control="12345">
            <segment Id="PID"><element Id="PID03">06009037</element><element Id="PID04" Composite="yes"><subelement Sequence="1">0100092582</subelement><subelement Sequence="2">0</subelement></element><element Id="PID05" Composite="yes"><subelement Sequence="1">CONSORT</subelement><subelement Sequence="2">WEB</subelement><subelement Sequence="3">TEST</subelement></element><element Id="PID07">19140122</element><element Id="PID08">M</element><element Id="PID18">060090376401</element></segment>
            <segment Id="PV1"><element Id="PV103" Composite="yes"><subelement Sequence="1">ACCM</subelement><subelement Sequence="2">AC33</subelement></element><element Id="PV139">10</element></segment>
            <segment Id="ORC"><element Id="ORC01">NW</element><element Id="ORC02">9013-0127</element><element Id="ORC03">578</element><element Id="ORC05">IP</element><element Id="ORC09">200301131347</element><element Id="ORC10">PACS ORDER MESSAGE</element></segment>
            <segment Id="OBR"><element Id="OBR02">73.01</element><element Id="OBR03">578</element><element Id="OBR04" Composite="yes"><subelement Sequence="1">20853</subelement><subelement Sequence="2">CT-ANGIO CHEST W AND/OR W/O CONTRAST</subelement></element><element Id="OBR16" Composite="yes"><subelement Sequence="1">1030</subelement><subelement Sequence="2">CALVERT</subelement><subelement Sequence="3">THOMAS</subelement><subelement Sequence="4">J</subelement><subelement Sequence="5">MD</subelement></element><element Id="OBR19">MAINCT</element><element Id="OBR27">200301131347</element></segment></transaction></group><group ApplSender="HCM" SendingFacility="TDS" ApplReceiver="PA000000" ReceivingFacility="IMAG" Date="20030113" Time="1348" Security="ANCILLARY INITIATED ORDERS" Type="ORM" TypeDesc="Order message" Event="O01" EventDesc="Order message (also RDE, RDS, RGV, RAS)" Control="23456" ProcessingId="P" SyntaxVersion="2.2"><transaction Type="ORM" Event="O01" Control="23456">
            <segment Id="PID"><element Id="PID03">06009037</element><element Id="PID04" Composite="yes"><subelement Sequence="1">0100464727</subelement><subelement Sequence="2">0</subelement></element><element Id="PID05" Composite="yes"><subelement Sequence="1">CONSORT</subelement><subelement Sequence="2">WEB</subelement><subelement Sequence="3">TEST</subelement></element><element Id="PID07">19140122</element><element Id="PID08">M</element><element Id="PID18">060090376401</element></segment>
            <segment Id="PV1"><element Id="PV103">MRI</element><element Id="PV139">10</element></segment>
            <segment Id="ORC"><element Id="ORC01">NW</element><element Id="ORC02">W013-0047</element><element Id="ORC03">579</element><element Id="ORC05">IP</element><element Id="ORC09">200301131348</element><element Id="ORC10">ANCILLARY INITIATED ORDERS</element></segment>
            <segment Id="OBR"><element Id="OBR02">1.01</element><element Id="OBR03">579</element><element Id="OBR04" Composite="yes"><subelement Sequence="1">11916</subelement><subelement Sequence="2">MR EXTREMITY SHOULDER</subelement></element><element Id="OBR16" Composite="yes"><subelement Sequence="1">0432</subelement><subelement Sequence="2">PHILLIPS</subelement><subelement Sequence="3">MD,</subelement><subelement Sequence="4">R</subelement><subelement Sequence="5">MACON</subelement></element><element Id="OBR19">MAINMR</element><element Id="OBR27">200301131348</element><element Id="OBR31">ROTATOR CUFF TEAR</element></segment></transaction></group><group ApplSender="HCM" SendingFacility="TDS" ApplReceiver="Broker" ReceivingFacility="PACS" Date="20030710" Time="151849" Type="ORM" TypeDesc="Order message" Event="O01" EventDesc="Order message (also RDE, RDS, RGV, RAS)" Control="34567" ProcessingId="P" SyntaxVersion="2.2"><transaction Type="ORM" Event="O01" Control="34567">
            <segment Id="PID"><element Id="PID03">06009037</element><element Id="PID05" Composite="yes"><subelement Sequence="1">CONSORT</subelement><subelement Sequence="2">WEB</subelement><subelement Sequence="3">TEST</subelement></element><element Id="PID18">060090376401</element></segment>
            <segment Id="ORC"><element Id="ORC01">SC</element><element Id="ORC03">578</element><element Id="ORC05">CM</element><element Id="ORC09">20030710</element></segment>
            <segment Id="OBR"><element Id="OBR03">578</element></segment></transaction></group><group ApplSender="HCM" SendingFacility="TDS" ApplReceiver="Broker" ReceivingFacility="PACS" Date="20030710" Time="151849" Type="ORM" TypeDesc="Order message" Event="O01" EventDesc="Order message (also RDE, RDS, RGV, RAS)" Control="45678" ProcessingId="P" SyntaxVersion="2.2"><transaction Type="ORM" Event="O01" Control="45678">
            <segment Id="PID"><element Id="PID03">06009037</element><element Id="PID05" Composite="yes"><subelement Sequence="1">CONSORT</subelement><subelement Sequence="2">WEB</subelement><subelement Sequence="3">TEST</subelement></element><element Id="PID18">060090376401</element></segment>
            <segment Id="ORC"><element Id="ORC01">SC</element><element Id="ORC03">579</element><element Id="ORC05">CM</element><element Id="ORC09">20030710</element></segment>
            <segment Id="OBR"><element Id="OBR03">579</element></segment></transaction></group><group ApplSender="HCM" SendingFacility="TDS" ApplReceiver="PA000000" ReceivingFacility="IMAG" Date="20030113" Time="1348" Security="ANCILLARY INITIATED ORDERS" Type="ORM" TypeDesc="Order message" Event="O01" EventDesc="Order message (also RDE, RDS, RGV, RAS)" Control="56789" ProcessingId="P" SyntaxVersion="2.2"><transaction Type="ORM" Event="O01" Control="56789">
            <segment Id="PID"><element Id="PID03">06009037</element><element Id="PID04" Composite="yes"><subelement Sequence="1">0100451210</subelement><subelement Sequence="2">0</subelement></element><element Id="PID05" Composite="yes"><subelement Sequence="1">CONSORT</subelement><subelement Sequence="2">WEB</subelement><subelement Sequence="3">TEST</subelement></element><element Id="PID07">19140122</element><element Id="PID08">M</element><element Id="PID18">060090376401</element></segment>
            <segment Id="PV1"><element Id="PV103">HICMM</element><element Id="PV139">10</element></segment>
            <segment Id="ORC"><element Id="ORC01">CA</element><element Id="ORC02">8013-0083</element><element Id="ORC03">580</element><element Id="ORC09">200301131348</element><element Id="ORC10">ANCILLARY INITIATED ORDERS</element></segment>
            <segment Id="OBR"><element Id="OBR02">4.00</element><element Id="OBR03">580</element><element Id="OBR04" Composite="yes"><subelement Sequence="1">10259</subelement><subelement Sequence="2">US BREAST LIMITED</subelement></element><element Id="OBR16" Composite="yes"/><element Id="OBR19">HICUS</element><element Id="OBR27">200301131348</element></segment></transaction></group></interchange></ediroot>            """;
}
