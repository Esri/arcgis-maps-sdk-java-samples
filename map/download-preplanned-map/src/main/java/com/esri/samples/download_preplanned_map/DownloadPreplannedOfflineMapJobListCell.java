package com.esri.samples.download_preplanned_map;

import com.esri.arcgisruntime.tasks.offlinemap.DownloadPreplannedOfflineMapJob;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;

/**
 * Convenience class for showing preplanned map jobs in a list view with their progress.
 */
public class DownloadPreplannedOfflineMapJobListCell extends ListCell<DownloadPreplannedOfflineMapJob> {

    @Override
    protected void updateItem(DownloadPreplannedOfflineMapJob downloadPreplannedOfflineMapJob, boolean empty) {
        super.updateItem(downloadPreplannedOfflineMapJob, empty);

        if (downloadPreplannedOfflineMapJob != null) {

            // show the job's progress with a progress indicator
            ProgressIndicator progressIndicator = new ProgressIndicator(downloadPreplannedOfflineMapJob.getProgress() / 100.0);

            downloadPreplannedOfflineMapJob.addJobChangedListener(() ->
                    progressIndicator.setProgress(downloadPreplannedOfflineMapJob.getProgress() / 100.0)
            );

            setGraphic(progressIndicator);
            setText(downloadPreplannedOfflineMapJob.getParameters().getPreplannedMapArea().getPortalItem().getTitle());
        } else {
            setGraphic(null);
            setText(null);
        }
    }
}
