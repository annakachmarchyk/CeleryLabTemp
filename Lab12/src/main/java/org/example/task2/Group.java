package org.example.task2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Group<T> extends Task<T> {
    private String groupUuid;
    private List<Task<T>> tasks;

    public Group<T> addTask(Task<T> task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
        return this;
    }

    @Override
    public void freeze() {
        super.freeze();
        groupUuid = UUID.randomUUID().toString();
        for (Task<T> task : tasks) {
            task.freeze();
        }
    }

    @Override
    public void apply(T arg, StampingVisitor visitor) {
        this.freeze();
        tasks = Collections.unmodifiableList(tasks);

        // Notify the visitor about the group start
        Map<String, Object> groupStartResult = visitor.onGroupStart(this, getHeaders());
        boolean inGroup = (boolean) groupStartResult.get("in_group");

        for (Task<T> task : tasks) {
            // Modify this part to handle different types of tasks
            if (task instanceof Signature) {
                // Notify the visitor about each signature
                Map<String, Object> signatureResult = task.accept(visitor);
                // Process the results as needed
            } else if (task instanceof Group) {
                // Handle nested groups if needed
                task.apply(arg, visitor);
            }
        }

        visitor.onGroupEnd(this, getHeaders());
    }
}
