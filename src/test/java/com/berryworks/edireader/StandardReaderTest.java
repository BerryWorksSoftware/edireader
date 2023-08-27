package com.berryworks.edireader;

import com.berryworks.edireader.tokenizer.Token;
import com.berryworks.edireader.tokenizer.Token.TokenType;
import com.berryworks.edireader.tokenizer.TokenImpl;
import com.berryworks.edireader.util.sax.ContextAwareSaxAdapter;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class StandardReaderTest {

    private StandardReader reader;

    @Test
    public void segmentElements_simple() throws SAXException {
        reader = new MyStandardReader();
        final MyContentHandler handler = new MyContentHandler();
        reader.setContentHandler(handler);

//      ...|abc||123^^^||
        reader.parseSegmentElement(new MyToken(TokenType.SIMPLE, "abc"));
        reader.parseSegmentElement(new MyToken(TokenType.EMPTY, null));
        reader.parseSegmentElement(new MyToken(TokenType.SUB_ELEMENT, "123", true, false));
        reader.parseSegmentElement(new MyToken(TokenType.SUB_EMPTY, null, false, false));
        reader.parseSegmentElement(new MyToken(TokenType.SUB_EMPTY, null, false, false));
        reader.parseSegmentElement(new MyToken(TokenType.SUB_EMPTY, null, false, true));
        reader.parseSegmentElement(new MyToken(TokenType.EMPTY, null));
        assertEquals("start(element, abc) end(element) start(element, null) start(subelement, 123) end(subelement) end(element)", handler.getLog());
    }

    @Test
    public void segmentElements_ambiguousComposite() throws SAXException {
        reader = new MyStandardReader();
        final MyContentHandler handler = new MyContentHandler();
        reader.setContentHandler(handler);

        //      ...|abc||123||
        // In this scenario, the 123 is actually the first sub-element of a composite.
        // You can't tell by looking, but we have a CompositeAwarePlugin that tells us so.
        reader.parseSegmentElement(new MyToken(TokenType.SIMPLE, "abc"));
        reader.parseSegmentElement(new MyToken(TokenType.EMPTY, null));
        reader.parseSegmentElement(new MyToken(TokenType.SUB_ELEMENT, "123", true, true));
        reader.parseSegmentElement(new MyToken(TokenType.EMPTY, null));
        assertEquals("start(element, abc) end(element) start(element, null) start(subelement, 123) end(subelement) end(element)", handler.getLog());
    }

    @Test
    public void segmentWithElements() throws SAXException, IOException {
        reader = new AnsiReader();
        final MyContentHandler handler = new MyContentHandler();
        reader.setContentHandler(handler);

        reader.parse(new InputSource(new StringReader(SMALL_834)));
        String log = handler.getLog();
        assertEquals("start(ediroot, null) start(interchange, null) " +
                "start(sender, null) start(address, null) end(address) end(sender) " +
                "start(receiver, null) start(address, null) end(address) end(receiver) " +
                "start(group, null) " +
                "start(transaction, null) " +
                "start(segment, null) " +
                "start(element, 00) end(element) start(element, 0158420020130310001) end(element) start(element, 20130312) end(element) start(element, 0206) end(element) start(element, PT) end(element) start(element, 2) end(element) end(segment) start(segment, null) start(element, 38) end(element) start(element, 500647166) end(element) " +
                "end(segment) " +
                "start(loop, null) " +
                "start(segment, null) " + // N1*P5
                "start(element, P5) end(element) start(element, OR-MMIS) end(element) start(element, FI) end(element) start(element, 930592162) end(element) " +
                "end(segment) " +
                "end(loop) " +
                "start(loop, null) " +
                "start(segment, null) " + // N1*IN
                "start(element, IN) end(element) start(element, FI) end(element) start(element, 455492679) end(element) " +
                "end(segment) " +
                "end(loop) " +
                "start(loop, null) " +
                "start(segment, null) " + // INS
                "start(element, Y) end(element) " +
                "start(element, 18) end(element) " +
                "start(element, 001) end(element) " +
                "start(element, AI) end(element) " +
                "start(element, A) end(element) " +
                "start(element, null) start(subelement, C) end(subelement) end(element) " +
                "start(element, AC) end(element) " +
                "start(element, N) end(element) " +
                "end(segment) " +
                "start(segment, null) " + // REF
                "start(element, 0F) end(element) start(element, REF OF) end(element) " +
                "end(segment) start(segment, null) start(element, 23) end(element) start(element, REF 23) end(element) " +
                "end(segment) " +
                "start(segment, null) " +
                "start(element, 3H) end(element) start(element, REF 3H) end(element) " +
                "end(segment) " +
                "start(segment, null) start(element, F6) end(element) start(element, REF F6) end(element) " +
                "end(segment) " +
                "start(segment, null) start(element, 356) end(element) start(element, D8) end(element) start(element, 20120901) end(element) " +
                "end(segment) " +
                "start(segment, null) start(element, 357) end(element) start(element, D8) end(element) start(element, 20130331) end(element) " +
                "end(segment) " +
                "start(loop, null) " +
                "start(segment, null) start(element, 74) end(element) start(element, 1) end(element) start(element, SUBSCRIBER LAST 1) end(element) start(element, SUBSCRIBER FIRST 1) end(element) start(element, M) end(element) start(element, 34) end(element) start(element, 544001234) end(element) " +
                "end(segment) " +
                "start(segment, null) start(element, IP) end(element) start(element, TE) end(element) start(element, 5554718931) end(element) " +
                "end(segment) " +
                "start(segment, null) start(element, SUBSCRIBER1 ADDRESS 1) end(element) end(segment) start(segment, null) start(element, GRANTS PASS) end(element) start(element, OR) end(element) start(element, 975260000) end(element) start(element, CY) end(element) start(element, 033) end(element) " +
                "end(segment) " +
                "start(segment, null) " +
                "start(element, D8) end(element) start(element, 19830719) end(element) start(element, F) end(element) start(element, null) start(subelement, C) end(subelement) start(subelement, RET) end(subelement) start(subelement, 2186-5) end(subelement) end(element) end(segment) start(segment, null) start(element, P3) end(element) start(element, 82.25) end(element) end(segment) start(segment, null) start(element, LE) end(element) start(element, ENG) end(element) start(element, 7) end(element) end(segment) start(segment, null) start(element, LE) end(element) start(element, ENG) end(element) start(element, 5) end(element) end(segment) end(loop) start(loop, null) start(segment, null) start(element, 70) end(element) start(element, 1) end(element) start(element, INCORRECT) end(element) start(element, FIRST) end(element) start(element, A) end(element) start(element, 34) end(element) start(element, 001223344) end(element) " +
                "end(segment) " +
                "start(segment, null) " +
                "start(element, D8) end(element) start(element, 19930620) end(element) start(element, F) end(element) end(segment) end(loop) start(loop, null) start(segment, null) start(element, 31) end(element) start(element, 1) end(element) end(segment) start(segment, null) start(element, RECIPIENT MAIL ADDRESS LINE 1) end(element) start(element, RECIPIENT MAIL ADDRESS LINE 2) end(element) end(segment) start(segment, null) start(element, SALEM) end(element) start(element, OR) end(element) start(element, 97301) end(element) end(segment) end(loop) start(loop, null) start(segment, null) start(element, QD) end(element) start(element, 1) end(element) start(element, RESPONSIBLE PARTY) end(element) start(element, SMITH) end(element) start(element, JOHN) end(element) end(segment) end(loop) start(loop, null) start(segment, null) start(element, GD) end(element) start(element, 1) end(element) start(element, COMPANY NAME 40 CHARACTER OF DATA) end(element) end(segment) start(loop, null) start(segment, null) start(element, 001) end(element) start(element, HMO) end(element) start(element, 12345678902012062020130415N) end(element) start(element, IND) end(element) end(segment) start(segment, null) start(element, 348) end(element) start(element, D8) end(element) start(element, 20120901) end(element) end(segment) start(segment, null) start(element, 17) end(element) start(element, D4) end(element) " +
                "end(segment) " +
                "start(loop, null) " +
                "start(segment, null) " +
                "start(element, U) end(element) start(element, D183) end(element) start(element, 5) end(element) end(segment) end(loop) start(loop, null) start(segment, null) start(element, U) end(element) start(element, J375) end(element) start(element, 5) end(element) end(segment) end(loop) start(segment, null) start(element, 2700) end(element) end(segment) start(loop, null) start(segment, null) start(element, 1) end(element) " +
                "end(segment) " +
                "end(loop) " +
                "end(loop) " +
                "end(loop) " +
                "end(loop) " +
                "start(loop, null) " +
                "start(segment, null) " + // N1*75
                "start(element, 75) end(element) start(element, NEWBORN INDICATOR) end(element) end(segment) end(loop) start(segment, null) start(element, ZZ) end(element) start(element, Y) end(element) end(segment) start(segment, null) start(element, 2) end(element) end(segment) start(loop, null) start(segment, null) start(element, 75) end(element) start(element, PREVIOUS PMP END DATE) end(element) end(segment) end(loop) start(segment, null) start(element, 007) end(element) start(element, D8) end(element) start(element, 20130615) end(element) end(segment) start(segment, null) start(element, 3) end(element) end(segment) start(loop, null) start(segment, null) start(element, 75) end(element) start(element, ALTERNATE FORMAT) end(element) " +
                "end(segment) " +
                "end(loop) " +
                "start(segment, null) " +
                "start(element, ZZ) end(element) start(element, 10) end(element) " +
                "end(segment) " +
                "start(segment, null) " +
                "start(element, 4) end(element) " +
                "end(segment) " +
                "start(loop, null) " +
                "start(segment, null) " +
                "start(element, 75) end(element) start(element, DUE DATE) end(element) " +
                "end(segment) " +
                "end(loop) " +
                "start(segment, null) start(element, 007) end(element) start(element, D8) end(element) start(element, 20130620) end(element) end(segment) start(segment, null) start(element, 5) end(element) " +
                "end(segment) " +
                "start(loop, null) " +
                "start(segment, null) " +
                "start(element, 75) end(element) start(element, BRANCH - WORKER) end(element) " +
                "end(segment) " +
                "end(loop) " +
                "start(segment, null) " +
                "start(element, 3L) end(element) start(element, 1234567) end(element) " +
                "end(segment) " +
                "start(segment, null) " +
                "start(element, 6) end(element) " +
                "end(segment) " +
                "start(loop, null) " +
                "start(segment, null) " +
                "start(element, 75) end(element) start(element, FIPS CODE) end(element) " +
                "end(segment) " +
                "end(loop) " +
                "start(segment, null) " +
                "start(element, 3L) end(element) start(element, 88) end(element) " +
                "end(segment) " +
                "start(segment, null) " +
                "start(element, 7) end(element) " +
                "end(segment) " +
                "start(loop, null) " +
                "start(segment, null) " +
                "start(element, 75) end(element) start(element, NATIVE AMERICAN HERITAGE CODE) end(element) " +
                "end(segment) " +
                "end(loop) " +
                "start(segment, null) " +
                "start(element, XX1) end(element) start(element, Y) end(element) end(segment) start(segment, null) start(element, 8) end(element) " +
                "end(segment) " +
                "start(loop, null) " +
                "start(segment, null) " +
                "start(element, 75) end(element) start(element, GROUP CODE) end(element) end(segment) end(loop) start(segment, null) start(element, XX1) end(element) start(element, A) end(element) " +
                "end(segment) " +
                "start(segment, null) start(element, 9) end(element) " +
                "end(segment) " +
                "start(loop, null) " +
                "start(segment, null) " +
                "start(element, 75) end(element) start(element, BENEFIT PLAN) end(element) " +
                "end(segment) " +
                "end(loop) " +
                "start(segment, null) " +
                "start(element, PID) end(element) start(element, BEN) end(element) end(segment) start(segment, null) start(element, 10) end(element) " +
                "end(segment) " +
                "start(loop, null) " +
                "start(segment, null) " +
                "start(element, 75) end(element) start(element, PROGRAM ELIGIBILITY CODE) end(element) end(segment) end(loop) start(segment, null) start(element, 17) end(element) start(element, 11) end(element) end(segment) start(segment, null) start(element, 11) end(element) " +
                "end(segment) " +
                "start(loop, null) " +
                "start(segment, null) " +
                "start(element, 75) end(element) start(element, SNRG) end(element) end(segment) end(loop) start(segment, null) start(element, XX1) end(element) start(element, 7) end(element) end(segment) start(segment, null) start(element, 12) end(element) end(segment) start(loop, null) start(segment, null) start(element, 75) end(element) start(element, TPL CODE) end(element) end(segment) end(loop) start(segment, null) start(element, 9X) end(element) start(element, 99) end(element) end(segment) start(segment, null) start(element, 13) end(element) end(segment) start(loop, null) start(segment, null) start(element, 75) end(element) start(element, END REASON) end(element) end(segment) end(loop) start(segment, null) start(element, 17) end(element) start(element, 11) end(element) end(segment) start(segment, null) start(element, 2700) end(element) end(segment) end(transaction) end(group) end(interchange) end(ediroot)", log);
    }

    private static class MyStandardReader extends StandardReader {
        @Override
        protected Token recognizeBeginning() throws IOException, SAXException {
            return null;
        }

        @Override
        protected Token parseInterchange(Token t) throws SAXException, IOException {
            return null;
        }

        @Override
        public void preview() throws EDISyntaxException, IOException {
        }
    }

    private static class MyToken extends TokenImpl {
        private final boolean isFirst;
        private final boolean isLast;

        public MyToken(TokenType type, String value) {
            this(type, value, false, false);
        }

        public MyToken(TokenType type, String value, boolean isFirst, boolean isLast) {
            super(null);
            setType(type);
            if (value != null)
                for (char c : value.toCharArray()) {
                    append(c);
                }
            this.isFirst = isFirst;
            this.isLast = isLast;
        }

        @Override
        public boolean isFirst() {
            return isFirst;
        }

        @Override
        public boolean isLast() {
            return isLast;
        }
    }

    private static class MyContentHandler extends ContextAwareSaxAdapter {
        private String name;
        private String data;
        private String log = "";

        @Override
        public void start(String uri, String name, String data, EDIAttributes attributes) throws SAXException {
            this.name = name;
            this.data = data;
            log += "start(" + name + ", " + data + ") ";
        }

        @Override
        public void end(String uri, String name) throws SAXException {
            this.name = name;
            log += "end(" + name + ") ";
        }

        public String getLog() {
            return log.trim();
        }
    }

    private class MyPluginController extends PluginController {
    }

    public static final String SMALL_834 = "ISA*00*          *00*          *ZZ*ORDHS          *ZZ*MB888880       *130312*0206*!*00501*000000238*0*P*:~\n" +
            "GS*BE*ORDHS*MB888880*20130312*020630*146*X*005010X220A1~\n" +
            "ST*834*146001*005010X220A1~\n" +
            "BGN*00*0158420020130310001*20130312*0206*PT***2~\n" +
            "REF*38*500647166 ~\n" +
            "N1*P5*OR-MMIS*FI*930592162~\n" +
            "N1*IN**FI*455492679~\n" +
            "INS*Y*18*001*AI*A*C**AC**N~\n" +
            "REF*0F*REF OF~\n" +
            "REF*23*REF 23~\n" +
            "REF*3H*REF 3H~\n" +
            "REF*F6*REF F6~\n" +
            "DTP*356*D8*20120901~\n" +
            "DTP*357*D8*20130331~\n" +
            "NM1*74*1*SUBSCRIBER LAST 1*SUBSCRIBER FIRST 1*M***34*544001234~\n" +
            "PER*IP**TE*5554718931~\n" +
            "N3*SUBSCRIBER1 ADDRESS 1~\n" +
            "N4*GRANTS PASS*OR*975260000**CY*033~\n" +
            "DMG*D8*19830719*F**C:RET:2186-5~\n" +
            "AMT*P3*82.25~\n" +
            "LUI*LE*ENG**7~\n" +
            "LUI*LE*ENG**5~\n" +
            "NM1*70*1*INCORRECT*FIRST*A***34*001223344~\n" +
            "DMG*D8*19930620*F~\n" +
            "NM1*31*1~\n" +
            "N3*RECIPIENT MAIL ADDRESS LINE 1*RECIPIENT MAIL ADDRESS LINE 2~\n" +
            "N4*SALEM*OR*97301~\n" +
            "NM1*QD**1*RESPONSIBLE PARTY*SMITH*JOHN~\n" +
            "NM1*GD*1*COMPANY NAME 40 CHARACTER OF DATA~\n" +
            "HD*001**HMO*12345678902012062020130415N*IND~\n" +
            "DTP*348*D8*20120901~\n" +
            "REF*17*D4~\n" +
            "COB*U*D183*5~\n" +
            "COB*U*J375*5~\n" +
            "LS*2700~\n" +
            "LX*1~\n" +
            "N1*75*NEWBORN INDICATOR~\n" +
            "REF*ZZ*Y~\n" +
            "LX*2~\n" +
            "N1*75*PREVIOUS PMP END DATE~\n" +
            "DTP*007*D8*20130615~\n" +
            "LX*3~\n" +
            "N1*75*ALTERNATE FORMAT~\n" +
            "REF*ZZ*10~\n" +
            "LX*4~\n" +
            "N1*75*DUE DATE~\n" +
            "DTP*007*D8*20130620~\n" +
            "LX*5~\n" +
            "N1*75*BRANCH - WORKER~\n" +
            "REF*3L*1234567~\n" +
            "LX*6~\n" +
            "N1*75*FIPS CODE~\n" +
            "REF*3L*88~\n" +
            "LX*7~\n" +
            "N1*75*NATIVE AMERICAN HERITAGE CODE~\n" +
            "REF*XX1*Y~\n" +
            "LX*8~\n" +
            "N1*75*GROUP CODE~\n" +
            "REF*XX1*A~\n" +
            "LX*9~\n" +
            "N1*75*BENEFIT PLAN~\n" +
            "REF*PID*BEN~\n" +
            "LX*10~\n" +
            "N1*75*PROGRAM ELIGIBILITY CODE~\n" +
            "REF*17*11~\n" +
            "LX*11~\n" +
            "N1*75*SNRG~\n" +
            "REF*XX1*7~\n" +
            "LX*12~\n" +
            "N1*75*TPL CODE~\n" +
            "REF*9X*99~\n" +
            "LX*13~\n" +
            "N1*75*END REASON~\n" +
            "REF*17*11~\n" +
            "LE*2700~\n" +
            "SE*74*146001~\n" +
            "GE*1*146~\n" +
            "IEA*1*000000238~";

}
