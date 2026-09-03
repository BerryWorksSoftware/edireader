package com.berryworks.edireader.hl7;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class HL7_ParsingTest {

    private HL7Reader reader;
    private InputSource source;
    private MyHandler handler;

    @Before
    public void setUp() {
        reader = new HL7Reader();
        handler = new MyHandler();
    }

//    @Ignore
    @Test
    public void canParse() throws IOException, SAXException {
        reader.setContentHandler(handler);
        source = new InputSource(new StringReader("""
                MSH|^~\\&|MEDITECH^WA||||201905011242||ADT^A08|54920716|P|2.4|||AL|NE|
                EVN||201905011242||||201905091242|
                PID|1||MN00708088^^^^MR^JHC~MN597007^^^^PI^JHC||HOWL^JOE^^^^^L~^^^^^^||19860522|F||RUSF|68 CHOOKS LINE^^BLOOMSBURY^QLD^4799||07 0134 5366^^^JOE.HOWL@anonemail.com||RUS|MAR|ORT|AN16311706|
                """));
        reader.parse(source);
        assertEquals("""
                        ediroot ()interchange (Standard=HL7)group (ApplSender=MEDITECH,Date=20190501,Time=1242,Type=ADT,TypeDesc=ADT message,Event=A08,EventDesc=Update patient information,Control=54920716,ProcessingId=P,SyntaxVersion=2.4,AckRequest=AL)transaction (Type=ADT,Event=A08,Control=54920716)segment (Id=EVN)element (Id=EVN02): 201905011242
                        element (Id=EVN06): 201905091242
                        
                        segment (Id=PID)element (Id=PID01): 1
                        element (Id=PID03,Composite=yes)subelement (Sequence=1): MN00708088
                        subelement (Sequence=5): MR
                        subelement (Sequence=6): JHC
                        
                        element (Id=PID03,Composite=yes)subelement (Sequence=1): MN597007
                        subelement (Sequence=5): PI
                        subelement (Sequence=6): JHC
                        
                        element (Id=PID05,Composite=yes)subelement (Sequence=1): HOWL
                        subelement (Sequence=2): JOE
                        subelement (Sequence=7): L
                        
                        element (Id=PID05,Composite=yes)
                        element (Id=PID07): 19860522
                        element (Id=PID08): F
                        element (Id=PID10): RUSF
                        element (Id=PID11,Composite=yes)subelement (Sequence=1): 68 CHOOKS LINE
                        subelement (Sequence=3): BLOOMSBURY
                        subelement (Sequence=4): QLD
                        subelement (Sequence=5): 4799
                        
                        element (Id=PID13,Composite=yes)subelement (Sequence=1): 07 0134 5366
                        subelement (Sequence=4): JOE.HOWL@anonemail.com
                        
                        element (Id=PID15): RUS
                        element (Id=PID16): MAR
                        element (Id=PID17): ORT
                        element (Id=PID18): AN16311706""",
                handler.getTrace().trim());
    }

    @Test
    public void handlesCompositeConflict() throws IOException, SAXException {
        reader.setContentHandler(handler);
        source = new InputSource(new StringReader("" +
                "MSH|^~\\&|MEDITECH^WA||||201905011242||AXX^A08|54920716|P|2.4|||AL|NE|\n" +
                "EVN||201905011242||||201905091242|\n" +
                // Via the plugin, we will indicate that PID03 is NOT a composite, even though it looks like one.
                "PID|1||MN00708088^^^^MR^JHC~MN597007^^^^PI^JHC||HOWL^JOE^^^^^L~^^^^^^||19860522|F||RUSF|68 CHOOKS LINE^^BLOOMSBURY^QLD^4799||07 0134 5366^^^JOE.HOWL@anonemail.com^^||RUS|MAR|ORT|AN16311706|\n"));
        reader.parse(source);
        assertEquals("""
                        ediroot ()interchange (Standard=HL7)group (ApplSender=MEDITECH,Date=20190501,Time=1242,Type=AXX,Event=A08,EventDesc=Update patient information,Control=54920716,ProcessingId=P,SyntaxVersion=2.4,AckRequest=AL)transaction (Type=AXX,Event=A08,Control=54920716)segment (Id=EVN)element (Id=EVN02): 201905011242
                        element (Id=EVN06): 201905091242
                        
                        segment (Id=PID)element (Id=PID01): 1
                        element (Id=PID03,Composite=yes)subelement (Sequence=1): MN00708088
                        subelement (Sequence=5): MR
                        subelement (Sequence=6): JHC
                        
                        element (Id=PID03,Composite=yes)subelement (Sequence=1): MN597007
                        subelement (Sequence=5): PI
                        subelement (Sequence=6): JHC
                        
                        element (Id=PID05,Composite=yes)subelement (Sequence=1): HOWL
                        subelement (Sequence=2): JOE
                        subelement (Sequence=7): L
                        
                        element (Id=PID05,Composite=yes)
                        element (Id=PID07): 19860522
                        element (Id=PID08): F
                        element (Id=PID10): RUSF
                        element (Id=PID11,Composite=yes)subelement (Sequence=1): 68 CHOOKS LINE
                        subelement (Sequence=3): BLOOMSBURY
                        subelement (Sequence=4): QLD
                        subelement (Sequence=5): 4799
                        
                        element (Id=PID13,Composite=yes)subelement (Sequence=1): 07 0134 5366
                        subelement (Sequence=4): JOE.HOWL@anonemail.com
                        
                        element (Id=PID15): RUS
                        element (Id=PID16): MAR
                        element (Id=PID17): ORT
                        element (Id=PID18): AN16311706""",
                handler.getTrace().trim());
    }

    class MyHandler extends DefaultHandler {

        private StringBuilder sb = new StringBuilder();

        String getTrace() {
            return sb.toString();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            sb.append(localName).append(' ');
            if (attributes != null) {
                int length = attributes.getLength();
                sb.append("(");
                for (int i = 0; i < length; i++) {
                    String name = attributes.getLocalName(i);
                    String value = attributes.getValue(i);
                    sb.append(name).append('=').append(value).append(',');
                }
                if (sb.toString().endsWith(",")) {
                    sb.setLength(sb.length() - 1);
                }
                sb.append(")");
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            sb.append('\n');
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            sb.append(": ").append(ch, start, length);
        }
    }
}

