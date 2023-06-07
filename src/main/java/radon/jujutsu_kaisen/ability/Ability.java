package radon.jujutsu_kaisen.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public abstract class Ability {
    public enum ActivationType {
        INSTANT,
        TOGGLED
    }

    public abstract ActivationType getActivationType();
    public abstract void runClient(LivingEntity entity);
    public abstract void runServer(LivingEntity entity);

    public Component getName() {
        ResourceLocation key = JujutsuAbilities.getKey(this);
        return Component.translatable(String.format("ability.%s.%s", key.getNamespace(), key.getPath()));
    }

    public abstract float getCost();

    public interface IToggled {
        default Component getEnableMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = JujutsuAbilities.getKey(ability);
            return Component.translatable(String.format("ability.%s.%s.enable", key.getNamespace(), key.getPath()));
        }

        default Component getDisableMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = JujutsuAbilities.getKey(ability);
            return Component.translatable(String.format("ability.%s.%s.disable", key.getNamespace(), key.getPath()));
        }
    }
}
