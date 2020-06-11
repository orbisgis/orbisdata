package org.orbisgis.orbisdata.processmanager.process

import org.junit.jupiter.api.Test

import static java.lang.Math.round

/**
 * Test class dedicated to {@link ProgressMonitor} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2020)
 */
class TestProgressMonitor {

    @Test
    void progressTest() {
        def pm1 = new ProgressMonitor("main", 7, true)
        def pm2 = new ProgressMonitor("main", 7)
        def pm3 = new ProgressMonitor(7, true)
        def pm4 = new ProgressMonitor(7)
        def pm5 = new ProgressMonitor("main", true)
        def pm6 = new ProgressMonitor(true)
        def pm7 = new ProgressMonitor("main")
        def pm8 = new ProgressMonitor()

        assert 7 == pm1.maxStep
        assert 7 == pm2.maxStep
        assert 7 == pm3.maxStep
        assert 7 == pm4.maxStep
        assert -1 == pm5.maxStep
        assert -1 == pm6.maxStep
        assert -1 == pm7.maxStep
        assert -1 == pm8.maxStep

        assert "main", pm1.name
        assert "main", pm2.name
        assert pm3.name
        assert pm4.name
        assert "main", pm5.name
        assert pm6.name
        assert "main", pm7.name
        assert pm8.name
    }

    @Test
    void progressWithMaxTest(){
        def list = [
                new ProgressMonitor("main", 7, true),
                new ProgressMonitor("main", 7),
                new ProgressMonitor(7, true),
                new ProgressMonitor(7)
        ]

        list.each { pm ->
            def p = 0
            def p1 = 0
            def p2 = 0
            def p3 = 0
            def p4 = 0
            def p5 = 0
            def p6 = 0
            def p7 = 0
            assert 7 == pm.maxStep
            assert 0.0 == pm.progress

            pm.incrementStep()
            p++
            assert round(100.0 / 7.0 * 1.0) == round(pm.progress)

            def subPm1 = pm.getSubProgress("sub1", 4, false)
            assert subPm1

            pm.incrementStep()
            p++
            pm.incrementStep()
            p++
            assert round(100.0 / 8.0 * 3.0) == round(pm.progress)

            def subPm2 = pm.getSubProgress("sub2", 3)
            assert subPm2
            def subPm3 = pm.getSubProgress("sub3", false)
            assert subPm3
            def subPm4 = pm.getSubProgress(4, false)
            assert subPm4
            def subPm5 = pm.getSubProgress("sub2")
            assert subPm5
            def subPm6 = pm.getSubProgress(false)
            assert subPm6
            def subPm7 = pm.getSubProgress(5)
            assert subPm7

            subPm1.incrementStep()
            p1++
            assert round(getProg(p, p1, p2, p3, p4, p5, p6, p7)) == round(pm.progress)

            subPm1.incrementStep()
            p1++
            subPm1.incrementStep()
            p1++
            assert round(getProg(p, p1, p2, p3, p4, p5, p6, p7)) == round(pm.progress)

            subPm2.incrementStep()
            p2++
            subPm2.incrementStep()
            p2++
            assert round(getProg(p, p1, p2, p3, p4, p5, p6, p7)) == round(pm.progress)

            subPm3.incrementStep()
            subPm3.incrementStep()
            subPm3.incrementStep()
            assert round(getProg(p, p1, p2, p3, p4, p5, p6, p7)) == round(pm.progress)

            subPm3.end()
            p3 = 1
            assert round(getProg(p, p1, p2, p3, p4, p5, p6, p7)) == round(pm.progress)

            subPm5.end()
            p5 = 1
            subPm6.end()
            p6 = 1
            assert round(getProg(p, p1, p2, p3, p4, p5, p6, p7)) == round(pm.progress)

            subPm7.end()
            p7 = 5
            assert round(getProg(p, p1, p2, p3, p4, p5, p6, p7)) == round(pm.progress)

            subPm1.incrementStep()
            p1++
            subPm2.incrementStep()
            p2++
            assert round(getProg(p, p1, p2, p3, p4, p5, p6, p7)) == round(pm.progress)

            pm.incrementStep()
            p++
            pm.incrementStep()
            p++
            assert round(getProg(p, p1, p2, p3, p4, p5, p6, p7)) == round(pm.progress)

            subPm4.end()
            p4 = 4
            assert round(getProg(p, p1, p2, p3, p4, p5, p6, p7)) == round(pm.progress)

            pm.incrementStep()
            pm.incrementStep()
            assert 100.0 == round(pm.progress)

            pm.incrementStep()
            pm.incrementStep()
            pm.incrementStep()
            assert 100.0 == round(pm.progress)
        }
    }

    @Test
    void progressWithoutMaxTest(){
        def list = [
                new ProgressMonitor("main", true),
                new ProgressMonitor(true),
                new ProgressMonitor("main"),
                new ProgressMonitor()
        ]

        list.each { pm ->
            assert -1 == pm.maxStep
            assert -1 == pm.progress

            pm.incrementStep()
            assert -1 == pm.progress

            def subPm1 = pm.getSubProgress("sub1", 4, false)
            assert subPm1

            pm.incrementStep()
            pm.incrementStep()
            assert -1, pm.progress

            def subPm2 = pm.getSubProgress("sub2", 3)
            assert subPm2
            def subPm3 = pm.getSubProgress("sub3", false)
            assert subPm3
            def subPm4 = pm.getSubProgress(4, false)
            assert subPm4
            def subPm5 = pm.getSubProgress("sub2")
            assert subPm5
            def subPm6 = pm.getSubProgress(false)
            assert subPm6
            def subPm7 = pm.getSubProgress(5)
            assert subPm7

            subPm1.incrementStep()
            assert -1 == pm.progress

            subPm1.incrementStep()
            subPm1.incrementStep()
            assert -1 == pm.progress

            subPm2.incrementStep()
            subPm2.incrementStep()
            assert -1 == pm.progress

            subPm3.incrementStep()
            subPm3.incrementStep()
            subPm3.incrementStep()
            assert -1 == pm.progress

            subPm3.end()
            assert -1 == pm.progress

            subPm5.end()
            subPm6.end()
            assert -1 == pm.progress

            subPm7.end()
            assert -1 == pm.progress

            subPm1.incrementStep()
            subPm2.incrementStep()
            assert -1 == pm.progress

            pm.incrementStep()
            pm.incrementStep()
            assert -1 == pm.progress

            subPm4.end()
            assert -1 == pm.progress

            pm.end()
            assert 100.0, round(pm.progress)
        }
    }

    private static double getProg(float p, float p1, float p2, float p3, float p4, float p5, float p6, float p7){
        if(p3 == 0 || p5 == 0 || p6 == 0){
            return -1
        }
        return (100.0/(7+7)*(p+p1/4+p2/3+p3+p4/4+p5+p6+p7/5))
    }
}