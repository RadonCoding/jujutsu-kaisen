package radon.jujutsu_kaisen.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;

import java.util.Optional;

public class SorcererUtil {
    public static SorcererGrade getGrade(float experience) {
        SorcererGrade result = SorcererGrade.GRADE_4;

        for (SorcererGrade grade : SorcererGrade.values()) {
            if (experience < grade.getRequiredExperience()) break;

            result = grade;
        }
        return result;
    }
}
