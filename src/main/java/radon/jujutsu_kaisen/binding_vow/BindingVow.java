package radon.jujutsu_kaisen.binding_vow;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.ability.JJKAbilities;

public class BindingVow {
    public Component getName() {
        ResourceLocation key = JJKBindingVows.getKey(this);
        return Component.translatable(String.format("binding_vow.%s.%s", key.getNamespace(), key.getPath()));
    }

    public Component getDescription() {
        ResourceLocation key = JJKBindingVows.getKey(this);
        return Component.translatable(String.format("binding_vow.%s.%s.description", key.getNamespace(), key.getPath()));
    }
}
