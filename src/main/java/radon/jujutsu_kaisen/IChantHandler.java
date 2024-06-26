package radon.jujutsu_kaisen;


import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface IChantHandler {
    List<String> getMessages(LivingEntity owner);
}
