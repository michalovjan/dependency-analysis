package org.jboss.pnc.core.builder.operationHandlers;

import org.jboss.pnc.core.BuildDriverFactory;
import org.jboss.pnc.core.builder.BuildTask;
import org.jboss.pnc.core.exception.CoreException;
import org.jboss.pnc.model.TaskStatus;
import org.jboss.pnc.model.builder.BuildDetails;
import org.jboss.pnc.spi.builddriver.BuildDriver;

import javax.inject.Inject;
import java.util.function.Consumer;

/**
 * Created by <a href="mailto:matejonnet@gmail.com">Matej Lazar</a> on 2014-12-03.
 */
public class StartBuildHandler extends OperationHandlerBase implements OperationHandler {

    private final BuildDriverFactory buildDriverFactory;

    @Inject
    public StartBuildHandler(BuildDriverFactory buildDriverFactory) {
        this.buildDriverFactory = buildDriverFactory;
    }

    @Override
    protected TaskStatus.Operation executeAfter() {
        return TaskStatus.Operation.CREATE_REPOSITORY;
    }

    @Override
    protected void doHandle(BuildTask buildTask) {
        buildTask.onStatusUpdate(new TaskStatus(TaskStatus.Operation.BUILD_SCHEDULED, TaskStatus.State.STARTED));
        try {
            Consumer<BuildDetails> onComplete = (buildDetails) -> {
                buildTask.onStatusUpdate(new TaskStatus(TaskStatus.Operation.BUILD_SCHEDULED, TaskStatus.State.COMPLETED));
                buildTask.setBuildDetails(buildDetails);
            };

            Consumer<Exception> onError = (e) -> {
                buildTask.onError(e);
            };

            //TODO better validation
            assert (buildTask.getProjectBuildConfiguration() != null);
            assert (buildTask.getRepositoryConfiguration() != null);

            BuildDriver buildDriver = buildDriverFactory.getBuildDriver(buildTask.getProjectBuildConfiguration().getEnvironment().getBuildType());
            buildDriver.startProjectBuild(buildTask.getProjectBuildConfiguration(), buildTask.getRepositoryConfiguration(),
                    onComplete, onError);

        } catch (CoreException e) {
            buildTask.onError(e);
        }
    }
}