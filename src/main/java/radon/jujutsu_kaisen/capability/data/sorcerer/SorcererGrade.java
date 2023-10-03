package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;


public enum SorcererGrade {
    GRADE_4(1.0F, 10.0F),
    GRADE_3(1.25F, 25.0F),
    SEMI_GRADE_2(1.5F, 50.0F),
    GRADE_2(1.75F, 75.0F),
    SEMI_GRADE_1(2.0F, 100.0F),
    GRADE_1(2.25F, 125.0F),
    SPECIAL_GRADE_1(2.5F, 150.0F),
    SPECIAL_GRADE(3.0F, 300.0F);

    private final float power;
    private final float reward;

    SorcererGrade(float power, float reward) {
        this.power = power;
        this.reward = reward;
    }

    public float getRequiredExperience() {
        return ConfigHolder.SERVER.getRequiredExperience().get(this);
    }

    public float getReward() {
        return this.reward;
    }

    public Component getName() {
        return Component.translatable(String.format("grade.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }

    public float getBasePower() {
        return this.power;
    }

    public float getRealPower(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return 0.0F;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return this.power * (cap.isInZone(owner) ? 1.20F : 1.0F);
    }
}
