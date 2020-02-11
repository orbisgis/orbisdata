package org.orbisgis.orbisdata.processmanager.process;

import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.processmanager.api.IProgressMonitor;

import static java.lang.Math.round;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link ProgressMonitor} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2020)
 */
class ProgressMonitorTest {

    @Test
    public void progressTest(){
        IProgressMonitor pm = new ProgressMonitor("main",7, true);
        assertEquals(7, pm.getMaxStep());
        assertEquals(0.0, pm.getProgress());

        pm.incrementStep();
        assertEquals(round(100.0/7.0*1.0), round(pm.getProgress()));

        IProgressMonitor subPm1 = pm.getSubProgress("sub1",4);
        assertNotNull(subPm1);

        pm.incrementStep();
        pm.incrementStep();
        assertEquals(round(100.0/7.0*3.0), round(pm.getProgress()));

        IProgressMonitor subPm2 = pm.getSubProgress("sub2",3);
        assertNotNull(subPm2);

        subPm1.incrementStep();
        assertEquals(round(100.0/7.0*(3.0+1.0/4.0)), round(pm.getProgress()));

        subPm1.incrementStep();
        subPm1.incrementStep();
        assertEquals(round(100.0/7.0*(3.0+3.0/4.0)), round(pm.getProgress()));

        subPm2.incrementStep();
        subPm2.incrementStep();
        assertEquals(round(100.0/7.0*(3.0+3.0/4.0+2.0/3.0)), round(pm.getProgress()));

        subPm1.incrementStep();
        subPm2.incrementStep();
        assertEquals(round(100.0/7.0*5.0), round(pm.getProgress()));

        subPm1.incrementStep();
        subPm2.incrementStep();
        assertEquals(round(100.0/7.0*5.0), round(pm.getProgress()));

        pm.incrementStep();
        pm.incrementStep();
        assertEquals(100.0, round(pm.getProgress()));

        pm.incrementStep();
        pm.incrementStep();
        assertEquals(100.0, round(pm.getProgress()));


        pm = new ProgressMonitor("main",7, false);
        assertEquals(7, pm.getMaxStep());
        assertEquals(0.0, pm.getProgress());

        pm.incrementStep();
        assertEquals(round(100.0/7.0*1.0), round(pm.getProgress()));

        subPm1 = pm.getSubProgress("sub1",4);
        assertNotNull(subPm1);

        pm.incrementStep();
        pm.incrementStep();
        assertEquals(round(100.0/7.0*3.0), round(pm.getProgress()));

        subPm2 = pm.getSubProgress("sub2",3);
        assertNotNull(subPm2);

        subPm1.incrementStep();
        assertEquals(round(100.0/7.0*(3.0+1.0/4.0)), round(pm.getProgress()));

        subPm1.incrementStep();
        subPm1.incrementStep();
        assertEquals(round(100.0/7.0*(3.0+3.0/4.0)), round(pm.getProgress()));

        subPm2.incrementStep();
        subPm2.incrementStep();
        assertEquals(round(100.0/7.0*(3.0+3.0/4.0+2.0/3.0)), round(pm.getProgress()));

        subPm1.incrementStep();
        subPm2.incrementStep();
        assertEquals(round(100.0/7.0*5.0), round(pm.getProgress()));

        subPm1.incrementStep();
        subPm2.incrementStep();
        assertEquals(round(100.0/7.0*5.0), round(pm.getProgress()));

        pm.incrementStep();
        pm.incrementStep();
        assertEquals(100.0, round(pm.getProgress()));

        pm.incrementStep();
        pm.incrementStep();
        assertEquals(100.0, round(pm.getProgress()));
    }
}