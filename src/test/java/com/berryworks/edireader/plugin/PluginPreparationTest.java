package com.berryworks.edireader.plugin;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PluginPreparationTest {

    @Test
    public void test() {
        LoopDescriptor a = new LoopDescriptor("LoopA", "AAA");
        LoopDescriptor b1= new LoopDescriptor("LoopB1", "BBB");
        LoopDescriptor b2= new LoopDescriptor("LoopB2", "BBB_withDescriptiveSuffix");
        LoopDescriptor c1= new LoopDescriptor("LoopC", "CCC_withDescriptiveSuffix");
        LoopDescriptor c2= new LoopDescriptor("LoopC", "CCC-withDescriptiveSuffix");
        LoopDescriptor c3= new LoopDescriptor("LoopC", "CCC.withDescriptiveSuffix");

        PluginPreparation p = new PluginPreparation(new LoopDescriptor[]{a, b1, b2, c1, c2, c3});

        Map<String, List<LoopDescriptor>> map = p.segmentMap;
        assertEquals(3, map.size());
        assertTrue(map.containsKey("AAA"));
        assertTrue(map.containsKey("BBB"));
        assertTrue(map.containsKey("CCC"));

        assertEquals(1, p.getList("AAA").size());
        assertEquals(2, p.getList("BBB").size());
        assertEquals(3, p.getList("CCC").size());
    }
}
