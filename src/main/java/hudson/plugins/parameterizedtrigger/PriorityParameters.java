package hudson.plugins.parameterizedtrigger;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Parameters for setting the build priority.
 */
public class PriorityParameters extends AbstractBuildParameters {

    @DataBoundConstructor
    public PriorityParameters() {
    }

    @Override
    public Action getAction(AbstractBuild<?, ?> build, TaskListener listener) throws IOException, InterruptedException, DontTriggerException {
        return new PriorityAction(PriorityAction.Priority.HIGH);
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AbstractBuildParameters> {
        @Override
        public String getDisplayName() {
            return "Schedule the build with high priority";
        }
    }
}
