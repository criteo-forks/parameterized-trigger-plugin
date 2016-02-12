package hudson.plugins.parameterizedtrigger;

import hudson.model.Queue;
import hudson.model.queue.QueueSorter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Jenkins queue sorter that takes priority into account.
 * Use {@link PriorityAction} to specify the build priority.
 */
public class PrioritizedQueueSorter extends QueueSorter {

    private final QueueSorter originalSorter;
    private final Comparator<Queue.BuildableItem> comparator;

    public PrioritizedQueueSorter(QueueSorter originalSorter) {
        this.originalSorter = originalSorter;
        this.comparator = new PrioritizedComparator();
    }

    @Override
    public void sortBuildableItems(List<Queue.BuildableItem> list) {
        if (originalSorter != null) {
            originalSorter.sortBuildableItems(list);
        }
        Collections.sort(list, comparator);
    }

    private static class PrioritizedComparator implements Comparator<Queue.BuildableItem> {
        public int compare(Queue.BuildableItem item1, Queue.BuildableItem item2) {
            return Integer.valueOf(getPriority(item1).getValue()).compareTo(getPriority(item2).getValue());
        }

        private PriorityAction.Priority getPriority(Queue.BuildableItem item) {
            PriorityAction action = item.getAction(PriorityAction.class);
            return action == null ? PriorityAction.Priority.NORMAL : action.getPriority();
        }
    }
}
