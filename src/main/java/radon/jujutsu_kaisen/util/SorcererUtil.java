package radon.jujutsu_kaisen.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.config.ConfigHolder;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SorcererUtil {
    public static SorcererGrade getGrade(float experience) {
        SorcererGrade result = SorcererGrade.GRADE_4;

        for (SorcererGrade grade : SorcererGrade.values()) {
            if (experience < grade.getRequiredExperience()) break;

            result = grade;
        }
        return result;
    }

    public static boolean isExperienced(float experience) {
        return experience >= ConfigHolder.SERVER.requiredExperienceForExperienced.get().floatValue();
    }

    public static float getPower(float experience) {
        return 1.0F + experience / 1500.0F;
    }
}
