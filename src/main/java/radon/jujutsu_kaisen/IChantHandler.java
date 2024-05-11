package radon.jujutsu_kaisen;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface IChantHandler {
    List<String> getMessages(LivingEntity owner);
}
