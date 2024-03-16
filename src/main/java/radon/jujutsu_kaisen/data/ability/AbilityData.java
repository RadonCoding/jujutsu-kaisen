package radon.jujutsu_kaisen.data.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.UnknownNullability;
import radon.jujutsu_kaisen.ability.AbilityStopEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.visual.ServerVisualHandler;

import javax.annotation.Nullable;
import java.util.*;

public class AbilityData implements IAbilityData {
    private final Set<Ability> toggled;

    private @Nullable Ability channeled;
    private int charge;

    private final List<DelayedTickEvent> delayedTickEvents;
    private final Map<Ability, Integer> cooldowns;
    private final Map<Ability, Integer> disrupted;
    private final Map<Ability, Integer> durations;

    private final LivingEntity owner;

    public AbilityData(LivingEntity owner) {
        this.owner = owner;

        this.toggled = new HashSet<>();
        this.delayedTickEvents = new ArrayList<>();
        this.cooldowns = new HashMap<>();
        this.disrupted = new HashMap<>();
        this.durations = new HashMap<>();
    }

    private void updateDisrupted() {
        Iterator<Map.Entry<Ability, Integer>> iter = this.disrupted.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            int remaining = entry.getValue();

            if (remaining > 0) {
                this.disrupted.put(entry.getKey(), --remaining);
            } else {
                iter.remove();
            }
        }
    }

    private void updateCooldowns() {
        Iterator<Map.Entry<Ability, Integer>> iter = this.cooldowns.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            int remaining = entry.getValue();

            if (remaining > 0) {
                this.cooldowns.put(entry.getKey(), --remaining);
            } else {
                iter.remove();
            }
        }
    }

    private void updateDurations() {
        Iterator<Map.Entry<Ability, Integer>> iter = this.durations.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            Ability ability = entry.getKey();

            if (!this.isChanneling(ability)) {
                iter.remove();
                continue;
            }

            int remaining = entry.getValue();

            if (remaining >= 0) {
                this.durations.put(entry.getKey(), --remaining);
            } else {
                if (ability instanceof Ability.IToggled) {
                    if (this.hasToggled(ability)) {
                        this.toggle(ability);
                    }
                } else if (ability instanceof Ability.IChannelened) {
                    if (this.isChanneling(ability)) {
                        this.channel(ability);
                    }
                }
                iter.remove();
            }
        }
    }

    private void updateTickEvents() {
        this.delayedTickEvents.removeIf(DelayedTickEvent::finished);

        for (DelayedTickEvent current : new ArrayList<>(this.delayedTickEvents)) {
            current.tick();

            if (current.finished()) {
                current.run();
            }
        }
    }

    private void updateToggled() {
        List<Ability> remove = new ArrayList<>();

        for (Ability ability : new ArrayList<>(this.toggled)) {
            if (this.disrupted.containsKey(ability)) continue;

            Ability.Status status = ability.isStillUsable(this.owner);

            if (status == Ability.Status.SUCCESS || ((status == Ability.Status.ENERGY || status == Ability.Status.COOLDOWN) && ability instanceof Ability.IAttack)) {
                ability.run(this.owner);

                ((Ability.IToggled) ability).applyModifiers(this.owner);
            } else {
                remove.add(ability);
            }
        }

        for (Ability ability : remove) {
            this.toggle(ability);
        }
    }

    private void updateChanneled() {
        if (this.channeled != null) {
            if (this.disrupted.containsKey(this.channeled)) return;

            Ability.Status status = this.channeled.isStillUsable(this.owner);

            if (status == Ability.Status.SUCCESS || ((status == Ability.Status.ENERGY || status == Ability.Status.COOLDOWN) && this.channeled instanceof Ability.IAttack)) {
                this.channeled.run(this.owner);
            } else {
                this.channel(this.channeled);
            }
            this.charge++;
        } else {
            this.charge = 0;
        }
    }

    @Override
    public void tick() {
        this.updateCooldowns();
        this.updateDisrupted();
        this.updateDurations();
        this.updateTickEvents();
        this.updateToggled();
        this.updateChanneled();
    }

    @Override
    public void attack(DamageSource source, LivingEntity target) {
        if (this.channeled instanceof Ability.IAttack attack) {
            if (this.channeled.getStatus(this.owner) == Ability.Status.SUCCESS && attack.attack(source, this.owner, target)) {
                this.channeled.charge(this.owner);
                this.charge = 0;
            }
        }

        for (Ability ability : this.toggled) {
            // In-case any of IAttack's kill the target just break the loop
            if (target.isDeadOrDying()) break;

            if (!(ability instanceof Ability.IAttack attack)) continue;
            if (ability.getStatus(this.owner) != Ability.Status.SUCCESS) continue;
            if (!attack.attack(source, this.owner, target)) continue;

            ability.charge(this.owner);
        }

        if (this.owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncAbilityDataS2CPacket(this.serializeNBT()), player);
        }
    }

    @Override
    public void toggle(Ability ability) {
        if (!this.owner.level().isClientSide && this.owner instanceof Player) {
            if (ability.shouldLog(this.owner)) {
                if (this.hasToggled(ability)) {
                    this.owner.sendSystemMessage(ability.getDisableMessage());
                } else {
                    this.owner.sendSystemMessage(ability.getEnableMessage());
                }
            }
        }

        if (this.toggled.contains(ability)) {
            this.toggled.remove(ability);

            ability.cooldown(this.owner);

            ((Ability.IToggled) ability).onDisabled(this.owner);

            ((Ability.IToggled) ability).removeModifiers(this.owner);

            NeoForge.EVENT_BUS.post(new AbilityStopEvent(this.owner, ability));
        } else {
            this.toggled.add(ability);

            ((Ability.IToggled) ability).onEnabled(this.owner);
        }

        ability.run(this.owner);

        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public boolean hasToggled(Ability ability) {
        return this.toggled.contains(ability) && !this.disrupted.containsKey(ability);
    }

    @Override
    public void clear() {
        this.toggled.clear();
        this.channeled = null;
    }

    @Override
    public Set<Ability> getToggled() {
        return this.toggled;
    }

    @Override
    public @Nullable Ability getChanneled() {
        return this.channeled;
    }

    @Override
    public void channel(@Nullable Ability ability) {
        if (this.channeled != null) {
            ((Ability.IChannelened) this.channeled).onStop(this.owner);

            if (this.channeled instanceof Ability.ICharged charged) {
                if (charged.onRelease(this.owner)) {
                    this.channeled.charge(this.owner);
                }
            }

            if (!this.owner.level().isClientSide && this.channeled.shouldLog(this.owner)) {
                this.owner.sendSystemMessage(this.channeled.getDisableMessage());
            }

            this.channeled.cooldown(this.owner);

            NeoForge.EVENT_BUS.post(new AbilityStopEvent(this.owner, ability));
        }

        if (this.channeled == ability) {
            this.channeled = null;
        } else {
            this.channeled = ability;

            if (this.channeled != null) {
                if (!this.owner.level().isClientSide && this.channeled.shouldLog(this.owner)) {
                    this.owner.sendSystemMessage(this.channeled.getEnableMessage());
                }
                this.channeled.run(this.owner);
            }
        }
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public boolean isChanneling(Ability ability) {
        return this.channeled == ability && !this.disrupted.containsKey(this.channeled);
    }

    @Override
    public int getCharge() {
        return this.charge;
    }

    @Override
    public void addCooldown(Ability ability) {
        this.cooldowns.put(ability, ability.getRealCooldown(this.owner));
    }

    @Override
    public void removeCooldown(Ability ability) {
        this.cooldowns.remove(ability);
    }

    @Override
    public int getRemainingCooldown(Ability ability) {
        return this.cooldowns.getOrDefault(ability, 0);
    }

    @Override
    public boolean isCooldownDone(Ability ability) {
        return !this.cooldowns.containsKey(ability);
    }

    @Override
    public void resetCooldowns() {
        this.cooldowns.clear();
    }

    @Override
    public void disrupt(Ability ability, int duration) {
        this.disrupted.put(ability, duration);
    }

    @Override
    public void addDuration(Ability ability) {
        this.durations.put(ability, ((Ability.IDurationable) ability).getRealDuration(this.owner));
    }

    @Override
    public void delayTickEvent(Runnable task, int delay) {
        this.delayedTickEvents.add(new DelayedTickEvent(task, delay));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        nbt.putInt("charge", this.charge);

        ListTag toggledTag = new ListTag();

        for (Ability ability : this.toggled) {
            ResourceLocation key = JJKAbilities.getKey(ability);

            if (key == null) continue;

            toggledTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("toggled", toggledTag);

        ListTag cooldownsTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.cooldowns.entrySet()) {
            ResourceLocation key = JJKAbilities.getKey(entry.getKey());

            if (key == null) continue;

            CompoundTag data = new CompoundTag();
            data.putString("identifier", key.toString());
            data.putInt("cooldown", entry.getValue());
            cooldownsTag.add(data);
        }
        nbt.put("cooldowns", cooldownsTag);

        ListTag disruptedTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.disrupted.entrySet()) {
            ResourceLocation key = JJKAbilities.getKey(entry.getKey());

            if (key == null) continue;

            CompoundTag data = new CompoundTag();
            data.putString("identifier", key.toString());
            data.putInt("duration", entry.getValue());
            disruptedTag.add(data);
        }
        nbt.put("disrupted", disruptedTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.charge = nbt.getInt("charge");

        this.toggled.clear();

        for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
            this.toggled.add(JJKAbilities.getValue(new ResourceLocation(key.getAsString())));
        }

        this.cooldowns.clear();

        for (Tag key : nbt.getList("cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) key;
            this.cooldowns.put(JJKAbilities.getValue(new ResourceLocation(data.getString("identifier"))),
                    data.getInt("cooldown"));
        }

        this.disrupted.clear();

        for (Tag key : nbt.getList("disrupted", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) key;
            this.disrupted.put(JJKAbilities.getValue(new ResourceLocation(data.getString("identifier"))),
                    data.getInt("duration"));
        }
    }
}
