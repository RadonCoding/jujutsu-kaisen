package radon.jujutsu_kaisen.data.ability;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

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

    public boolean finished() {
        return this.delay == 0;
    }

    public void run() {
        this.task.run();
    }
}