package com.berryworks.edireader.hl7;

import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.util.Conversion;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class HL7ReaderTest {

    @Test
    public void canParseHL7() throws EDISyntaxException, IOException, TransformerException {
        StringReader stringReader = new StringReader(HL7_SAMPLE);
        StringWriter xmlOutput = new StringWriter();
        Conversion.ediToXml(stringReader, xmlOutput, new HL7Reader());
        String expected = HL7_XML.replace("\n", "");
        String actual = xmlOutput.toString().replace("\n", "");
        Assert.assertEquals(expected, actual);
    }

    @Ignore
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
