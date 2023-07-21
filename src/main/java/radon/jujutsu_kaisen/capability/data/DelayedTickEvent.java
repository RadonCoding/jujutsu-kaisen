package radon.jujutsu_kaisen.capability.data;

public class DelayedTickEvent {
    private final Runnable task;
    private int delay;

    public DelayedTickEvent(Runnable task, int delay) {
        this.task = task;
        this.delay = delay;
    }

    public void tick() {
        this.delay--;
    }

    public boolean run() {
        if (this.delay <= 0) {
            this.task.run();
            return true;
        }
        return false;
    }
}