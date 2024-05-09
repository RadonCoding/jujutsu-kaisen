package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public interface ITransformation {
    enum Part {
        HEAD,
        RIGHT_ARM,
        LEFT_ARM,
        LEGS,
        BODY;

        public EquipmentSlot getSlot() {
            return switch (this) {
                case HEAD -> EquipmentSlot.HEAD;
                case BODY, RIGHT_ARM, LEFT_ARM -> EquipmentSlot.CHEST;
                case LEGS -> EquipmentSlot.LEGS;
            };
        }
    }

    boolean isReplacement();

    Item getItem();

    Part getBodyPart();

    void onRightClick(LivingEntity owner);

    default float getSlimTranslation() {
        return 0.0F;
    }
}
