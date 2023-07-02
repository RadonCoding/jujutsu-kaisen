package radon.jujutsu_kaisen.capability.data;

import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class DelayedTickEvent {
    private final Consumer<LivingEntity> task;
    private int delay;

    public DelayedTickEvent(Consumer<LivingEntity> task, int delay) {
        this.task = task;
        this.delay = delay;
    }

    public void tick() {
        this.delay--;
    }

    public boolean run(LivingEntity entity) {
        if (this.delay <= 0) {
            this.task.accept(entity);
            return true;
        }
        return false;
    }
}