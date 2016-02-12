package hudson.plugins.parameterizedtrigger;

import hudson.Util;
import hudson.model.Action;
import hudson.model.InvisibleAction;
import hudson.model.Queue;

import java.util.List;

/**
 * Sets the priority for the build in the queue.
 * Works in conjunction with {@link PrioritizedQueueSorter}.
 */
public class PriorityAction extends InvisibleAction implements Queue.QueueAction {

    public enum Priority {
        HIGH(-1),
        NORMAL(0);

        private final int value;

        Priority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private final Priority priority;

    public PriorityAction(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean shouldSchedule(List<Action> list) {
        return Util.filter(list, PriorityAction.class).isEmpty();
    }
}
