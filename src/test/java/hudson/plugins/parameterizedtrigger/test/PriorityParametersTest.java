package hudson.plugins.parameterizedtrigger.test;

import hudson.model.Cause;
import hudson.model.Project;
import hudson.plugins.parameterizedtrigger.*;
import hudson.slaves.DumbSlave;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.SleepBuilder;

public class PriorityParametersTest extends HudsonTestCase {

    public void test() throws Exception {
        testCore(false);
    }

    public void testSamePriority() throws Exception {
        testCore(true);
    }

    private void testCore(boolean raiseProjectBPriority) throws Exception {
        DumbSlave slave = createOnlineSlave();

        Project<?, ?> projectA = createFreeStyleProject("projectA");
        Project<?, ?> projectA2 = createFreeStyleProject("projectA2");
        Project<?, ?> projectB = createFreeStyleProject("projectB");

        projectA.getBuildersList().add(
                new SleepBuilder(4000));
        projectA.getPublishersList().add(
                new BuildTrigger(new BuildTriggerConfig("projectA2", ResultCondition.SUCCESS, new PriorityParameters())));
        projectA.setAssignedNode(slave);
        projectA2.setAssignedNode(slave);
        projectB.setAssignedNode(slave);
        hudson.rebuildDependencyGraph();

        projectA2.setQuietPeriod(0);

        // Schedule project A to run immediately and project B to run in 3 seconds.
        // Project A takes 4 seconds to finish and then triggers project A2.
        projectA.scheduleBuild2(0);
        if (raiseProjectBPriority) {
            // Project A2 should complete after project B, since project B has the same (high) priority.
            projectB.scheduleBuild2(3, new Cause.UserIdCause(), new PriorityAction(PriorityAction.Priority.HIGH)).get();
        } else {
            // Project A2 should complete before project B, even though it would be scheduled later.
            projectB.scheduleBuild2(3).get();
        }

        waitUntilNoActivity();

        assertEquals(1, projectA.getBuilds().size());
        assertEquals(1, projectA2.getBuilds().size());
        assertEquals(1, projectB.getBuilds().size());

        boolean projectA2FinishedFirst = projectA2.getFirstBuild().getTimestamp().before(projectB.getFirstBuild().getTimestamp());
        if (raiseProjectBPriority) {
            assertFalse(projectA2FinishedFirst);
        } else {
            assertTrue(projectA2FinishedFirst);
        }
    }
}
