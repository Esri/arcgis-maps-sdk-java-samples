import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.samples.min_max_scale.MinMaxScaleSample;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

public class MinMaxScaleSampleTest extends FxRobot {

    private MapView mapView;

    @Before
    public void setup() throws Exception {
        ApplicationTest.launch(MinMaxScaleSample.class);
        mapView = lookup(n -> n instanceof MapView).query();
    }

    @After
    public void cleanup() throws Exception {
        FxToolkit.cleanupStages();
    }

    /**
     * Tests adding a statistic definition to the table that is not already in the table. The number of items in the table should increment.
     */
    @Test
    public void testScrollBoundByScale() {
        ArcGISMap map = mapView.getMap();

        double minScale = map.getMinScale();
        double maxScale = map.getMaxScale();
        double delta = 0.00001;

        moveTo(mapView);

        sleep(1000);

        while(mapView.getMapScale() < minScale) {
            robotContext().getMouseRobot().scroll(2);
            sleep(200);
        }

        Assert.assertEquals(mapView.getMapScale(), minScale, delta);

        while(mapView.getMapScale() > maxScale + delta) {
            robotContext().getMouseRobot().scroll(-2);
            sleep(200);
        }

        Assert.assertEquals(mapView.getMapScale(), maxScale, delta);
    }


}
