package org.orbisgis.orbisdata.processmanager.process;

import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.processmanager.api.IProgressMonitor;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class dedicated to {@link ProgressMonitor} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2020)
 */
class ProgressMonitorTest {

    @Test
    public void progressTest() {
        IProgressMonitor pm1 = new ProgressMonitor("main", 7, true);
        IProgressMonitor pm2 = new ProgressMonitor("main", 7);
        IProgressMonitor pm3 = new ProgressMonitor(7, true);
        IProgressMonitor pm4 = new ProgressMonitor(7);
        IProgressMonitor pm5 = new ProgressMonitor("main", true);
        IProgressMonitor pm6 = new ProgressMonitor(true);
        IProgressMonitor pm7 = new ProgressMonitor("main");
        IProgressMonitor pm8 = new ProgressMonitor();

        assertEquals(7, pm1.getMaxStep());
        assertEquals(7, pm2.getMaxStep());
        assertEquals(7, pm3.getMaxStep());
        assertEquals(7, pm4.getMaxStep());
        assertEquals(-1, pm5.getMaxStep());
        assertEquals(-1, pm6.getMaxStep());
        assertEquals(-1, pm7.getMaxStep());
        assertEquals(-1, pm8.getMaxStep());

        assertEquals("main", pm1.getName());
        assertEquals("main", pm2.getName());
        assertNotNull(pm3.getName());
        assertNotNull(pm4.getName());
        assertEquals("main", pm5.getName());
        assertNotNull(pm6.getName());
        assertEquals("main", pm7.getName());
        assertNotNull(pm8.getName());
    }

    @Test
    public void progressWithMaxTest(){
        List<IProgressMonitor> list = new ArrayList<>();
        list.add(new ProgressMonitor("main", 7, true));
        list.add(new ProgressMonitor("main", 7));
        list.add(new ProgressMonitor(7, true));
        list.add(new ProgressMonitor(7));

        for(IProgressMonitor pm : list) {
            float p = 0;
            float p1 = 0;
            float p2 = 0;
            float p3 = 0;
            float p4 = 0;
            float p5 = 0;
            float p6 = 0;
            float p7 = 0;
            assertEquals(7, pm.getMaxStep());
            assertEquals(0.0, pm.getProgress());

            pm.incrementStep();
            p++;
            assertEquals(round(100.0 / 7.0 * 1.0), round(pm.getProgress()));

            IProgressMonitor subPm1 = pm.getSubProgress("sub1", 4, false);
            assertNotNull(subPm1);

            pm.incrementStep();
            p++;
            pm.incrementStep();
            p++;
            assertEquals(round(100.0 / 8.0 * 3.0), round(pm.getProgress()));

            IProgressMonitor subPm2 = pm.getSubProgress("sub2", 3);
            assertNotNull(subPm2);
            IProgressMonitor subPm3 = pm.getSubProgress("sub3", false);
            assertNotNull(subPm3);
            IProgressMonitor subPm4 = pm.getSubProgress(4, false);
            assertNotNull(subPm4);
            IProgressMonitor subPm5 = pm.getSubProgress("sub2");
            assertNotNull(subPm5);
            IProgressMonitor subPm6 = pm.getSubProgress(false);
            assertNotNull(subPm6);
            IProgressMonitor subPm7 = pm.getSubProgress(5);
            assertNotNull(subPm7);

            subPm1.incrementStep();
            p1++;
            assertEquals(round(getProg(p, p1, p2, p3, p4, p5, p6, p7)), round(pm.getProgress()));

            subPm1.incrementStep();
            p1++;
            subPm1.incrementStep();
            p1++;
            assertEquals(round(getProg(p, p1, p2, p3, p4, p5, p6, p7)), round(pm.getProgress()));

            subPm2.incrementStep();
            p2++;
            subPm2.incrementStep();
            p2++;
            assertEquals(round(getProg(p, p1, p2, p3, p4, p5, p6, p7)), round(pm.getProgress()));

            subPm3.incrementStep();
            subPm3.incrementStep();
            subPm3.incrementStep();
            assertEquals(round(getProg(p, p1, p2, p3, p4, p5, p6, p7)), round(pm.getProgress()));

            subPm3.end();
            p3 = 1;
            assertEquals(round(getProg(p, p1, p2, p3, p4, p5, p6, p7)), round(pm.getProgress()));

            subPm5.end();
            p5 = 1;
            subPm6.end();
            p6 = 1;
            assertEquals(round(getProg(p, p1, p2, p3, p4, p5, p6, p7)), round(pm.getProgress()));

            subPm7.end();
            p7 = 5;
            assertEquals(round(getProg(p, p1, p2, p3, p4, p5, p6, p7)), round(pm.getProgress()));

            subPm1.incrementStep();
            p1++;
            subPm2.incrementStep();
            p2++;
            assertEquals(round(getProg(p, p1, p2, p3, p4, p5, p6, p7)), round(pm.getProgress()));

            pm.incrementStep();
            p++;
            pm.incrementStep();
            p++;
            assertEquals(round(getProg(p, p1, p2, p3, p4, p5, p6, p7)), round(pm.getProgress()));

            subPm4.end();
            p4 = 4;
            assertEquals(round(getProg(p, p1, p2, p3, p4, p5, p6, p7)), round(pm.getProgress()));

            pm.incrementStep();
            pm.incrementStep();
            assertEquals(100.0, round(pm.getProgress()));

            pm.incrementStep();
            pm.incrementStep();
            pm.incrementStep();
            assertEquals(100.0, round(pm.getProgress()));
        }
    }

    @Test
    public void progressWithoutMaxTest(){
        List<IProgressMonitor> list = new ArrayList<>();
        list.add(new ProgressMonitor("main", true));
        list.add(new ProgressMonitor(true));
        list.add(new ProgressMonitor("main"));
        list.add(new ProgressMonitor());

        for(IProgressMonitor pm : list) {
            assertEquals(-1, pm.getMaxStep());
            assertEquals(-1, pm.getProgress());

            pm.incrementStep();
            assertEquals(-1, pm.getProgress());

            IProgressMonitor subPm1 = pm.getSubProgress("sub1", 4, false);
            assertNotNull(subPm1);

            pm.incrementStep();
            pm.incrementStep();
            assertEquals(-1, pm.getProgress());

            IProgressMonitor subPm2 = pm.getSubProgress("sub2", 3);
            assertNotNull(subPm2);
            IProgressMonitor subPm3 = pm.getSubProgress("sub3", false);
            assertNotNull(subPm3);
            IProgressMonitor subPm4 = pm.getSubProgress(4, false);
            assertNotNull(subPm4);
            IProgressMonitor subPm5 = pm.getSubProgress("sub2");
            assertNotNull(subPm5);
            IProgressMonitor subPm6 = pm.getSubProgress(false);
            assertNotNull(subPm6);
            IProgressMonitor subPm7 = pm.getSubProgress(5);
            assertNotNull(subPm7);

            subPm1.incrementStep();
            assertEquals(-1, pm.getProgress());

            subPm1.incrementStep();
            subPm1.incrementStep();
            assertEquals(-1, pm.getProgress());

            subPm2.incrementStep();
            subPm2.incrementStep();
            assertEquals(-1, pm.getProgress());

            subPm3.incrementStep();
            subPm3.incrementStep();
            subPm3.incrementStep();
            assertEquals(-1, pm.getProgress());

            subPm3.end();
            assertEquals(-1, pm.getProgress());

            subPm5.end();
            subPm6.end();
            assertEquals(-1, pm.getProgress());

            subPm7.end();
            assertEquals(-1, pm.getProgress());

            subPm1.incrementStep();
            subPm2.incrementStep();
            assertEquals(-1, pm.getProgress());

            pm.incrementStep();
            pm.incrementStep();
            assertEquals(-1, pm.getProgress());

            subPm4.end();
            assertEquals(-1, pm.getProgress());

            pm.end();
            assertEquals(100.0, round(pm.getProgress()));
        }
    }

    private double getProg(float p, float p1, float p2, float p3, float p4, float p5, float p6, float p7){
        if(p3 == 0 || p5 == 0 || p6 == 0){
            return -1;
        }
        return (100.0/(7+7)*(p+p1/4+p2/3+p3+p4/4+p5+p6+p7/5));
    }
}