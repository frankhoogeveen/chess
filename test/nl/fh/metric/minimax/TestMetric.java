/*
 * License: GPL v3
 * 
 */

package nl.fh.metric.minimax;

import nl.fh.player.evalplayer.Metric;

/**
 * 
 * 
 */
public class TestMetric implements Metric<TestTree> {

    public TestMetric() {
    }

    @Override
    public double eval(TestTree t) {
        return t.getContent();
    }


}