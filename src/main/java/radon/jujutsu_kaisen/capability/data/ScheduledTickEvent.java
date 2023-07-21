package radon.jujutsu_kaisen.capability.data;

import java.util.concurrent.Callable;

public class ScheduledTickEvent {
    private final Callable<Boolean> task;
    private int duration;

    public ScheduledTickEvent(Callable<Boolean> task, int delay) {
        this.task = task;
        this.duration = delay;
    }

    public void tick() {
        this.duration--;
    }

    public boolean run() {
        if (this.duration > 0) {
            try {
                return this.task.call();
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}