import com.esri.samples.statistical_query_group_and_sort.StatisticalQueryGroupAndSortSample;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.DebugUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticalQueryGroupAndSortSampleTest extends FxRobot {

    private TableView statisticDefinitionTableView;
    private ComboBox fieldNameComboBox;
    private ComboBox statisticTypeComboBox;

    @Before
    public void setup() throws Exception {
        ApplicationTest.launch(StatisticalQueryGroupAndSortSample.class);

        statisticDefinitionTableView = lookup("#statisticDefinitionsTableView").queryTableView();
        fieldNameComboBox = lookup("#fieldNameComboBox").queryComboBox();
        statisticTypeComboBox = lookup("#statisticTypeComboBox").queryComboBox();

        // wait for initialization
        sleep(1000);
    }

    @After
    public void cleanup() throws Exception {
        FxToolkit.cleanupStages();
    }

    /**
     * Tests adding a statistic definition to the table that is not already in the table. The number of items in the table should increment.
     */
    @Test
    public void testAddNewFieldStatistic() {
        int statisticsDefinitionsCount = statisticDefinitionTableView.getItems().size();

        clickOn(fieldNameComboBox)
                .type(KeyCode.UP)
                .type(KeyCode.ENTER);

        clickOn(statisticTypeComboBox)
                .type(KeyCode.UP)
                .type(KeyCode.ENTER);

        clickOn("Add");

        Assert.assertEquals(statisticDefinitionTableView.getItems().size(), statisticsDefinitionsCount + 1);
    }

    /**
     * Tests adding a statistic definition to the table which is already in the table. A warning should display.
     */
    @Test
    public void testAddExistingFieldStatistic() {
        clickOn(statisticTypeComboBox)
                .type(KeyCode.DOWN)
                .type(KeyCode.ENTER);

        clickOn("Add");

        // create list to reverse from unmodifiable list
        final List<Window> windows = new ArrayList<>(robotContext().getWindowFinder().listWindows());
        // order top to bottom
        Collections.reverse(windows);

        Assert.assertEquals(windows.size(), 2);

        Stage alertStage = (Stage) windows.stream()
                .filter(window -> window instanceof Stage)
                .filter(window -> ((Stage) window).getModality() == Modality.APPLICATION_MODAL)
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(alertStage);

        final DialogPane alertDialogPane = (DialogPane) alertStage.getScene().getRoot();
        FxAssert.verifyThat(alertDialogPane, node -> "The select combination has already been chosen.".equals(node.getContentText()), errorMessageMapper -> {
            Image screenshot = DebugUtils.captureNode(alertDialogPane).apply(robotContext().getCaptureSupport());
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(screenshot, null), "png", new File("./build/Screenshot.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return errorMessageMapper;
        });
    }


}
