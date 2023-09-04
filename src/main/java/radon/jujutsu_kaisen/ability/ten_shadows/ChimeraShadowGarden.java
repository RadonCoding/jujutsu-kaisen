package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.ChimeraShadowGardenEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;

import java.util.ArrayList;
import java.util.List;

public class ChimeraShadowGarden extends DomainExpansion implements DomainExpansion.IOpenDomain {
    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity) {

    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {

    }

    @Override
    protected void createBarrier(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            int width = this.getWidth();
            int height = this.getHeight();

            ChimeraShadowGardenEntity domain = new ChimeraShadowGardenEntity(owner, this, width, height,
                    cap.getGrade().getPower(owner) + (cap.hasTrait(Trait.STRONGEST) ? 1.0F : 0.0F));
            owner.level.addFreshEntity(domain);

            cap.setDomain(domain);

            if (owner.level instanceof ServerLevel level) {
                List<TenShadowsSummon> summons = new ArrayList<>();

                for (Entity entity : cap.getSummons(level)) {
                    if (entity instanceof TenShadowsSummon summon && summon.isTame()) summons.add(summon);
                }

                for (TenShadowsSummon summon : summons) {
                    if (summons.stream().noneMatch(x -> x.getJujutsuType() == summon.getJujutsuType() && x.isClone())) {
                        summon.getAbility().spawn(owner, true);
                    }
                }
            }
        });
    }

    @Override
    public int getWidth() {
        return 32;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        /*@SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity victim = event.getEntity();
            Entity attacker = event.getSource().getEntity();

            if (attacker == null) return;

            if (victim.level instanceof ServerLevel level) {
                victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    if (cap.getDomain(level) instanceof ChimeraShadowGardenEntity domain && domain.isInsideBarrier(attacker.blockPosition())) {
                        for (int i = 0; i < 16; i++) {
                            for (int j = 0; j < victim.getBbHeight() * victim.getBbHeight(); j++) {
                                victim.level.addParticle(ParticleTypes.SMOKE, victim.getX() + (victim.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F),
                                        victim.getY(), victim.getZ() + (victim.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F),
                                        HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D,
                                        HelperMethods.RANDOM.nextGaussian() * 0.075D);
                                victim.level.addParticle(ParticleTypes.LARGE_SMOKE, victim.getX() + (victim.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F),
                                        victim.getY(), victim.getZ() + (victim.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F),
                                        HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D,
                                        HelperMethods.RANDOM.nextGaussian() * 0.075D);
                            }
                        }
                        victim.setPos(attacker.position().subtract(attacker.getLookAngle()));
                    }
                });
            }
        }*/

        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent event) {
            LivingEntity owner = event.getEntity();

            if (JJKAbilities.hasToggled(owner, JJKAbilities.CHIMERA_SHADOW_GARDEN.get())) {
                if (event.getAbility() instanceof Summon<?> summon && summon.isTamed(owner)) {
                    summon.spawn(owner, true);
                }
            }
        }
    }
}
