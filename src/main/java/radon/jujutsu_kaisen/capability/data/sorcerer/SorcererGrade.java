package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

import java.util.concurrent.atomic.AtomicReference;

public enum SorcererGrade {
    GRADE_4(0.0F, 1.0F, 10.0F),
    GRADE_3(100.0F, 1.25F, 25.0F),
    SEMI_GRADE_2(300.0F, 1.5F, 50.0F),
    GRADE_2(500.0F, 1.75F, 75.0F),
    SEMI_GRADE_1(1000.0F, 2.0F, 100.0F),
    GRADE_1(1500.0F, 2.25F, 125.0F),
    SPECIAL_GRADE_1(2000.0F, 2.5F, 150.0F),
    SPECIAL_GRADE(2500.0F, 3.0F, 300.0F);

    private final float required;
    private final float power;
    private final float reward;

    SorcererGrade(float required, float power, float reward) {
        this.required = required;
        this.power = power;
        this.reward = reward;
    }

    public float getRequiredExperience() {
        return this.required;
    }

    public float getReward() {
        return this.reward;
    }

    public Component getName() {
        return Component.translatable(String.format("grade.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }

    public float getPower() {
        return this.power;
    }

    public float getPower(LivingEntity owner) {
        AtomicReference<Float> result = new AtomicReference<>(this.power);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.getAndUpdate(power -> power * (cap.isInZone(owner) ? 1.20F : 1.0F)));
        return result.get();
    }
}
