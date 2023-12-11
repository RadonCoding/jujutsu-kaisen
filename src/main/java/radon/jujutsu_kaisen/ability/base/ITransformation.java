package radon.jujutsu_kaisen.ability.base;

import net.minecraft.world.item.Item;

public interface ITransformation {
    enum Part {
        HEAD,
        RIGHT_ARM,
        LEFT_ARM,
        RIGHT_LEG,
        LEFT_LEG,
        BODY
    }

    Item getItem();

    Part getBodyPart();

    void onRightClick();
}
