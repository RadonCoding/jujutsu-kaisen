package radon.jujutsu_kaisen.client.render.entity.effect;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.client.JJKRenderTypes;

import java.util.*;

// https://github.com/mekanism/Mekanism/blob/296c1ded7c05b5334390495c09f6b714abd0aedc/src/main/java/mekanism/client/render/lib/effect/BoltRenderer.java
public class BoltRenderer {
    private static final float REFRESH_TIME = 3F;
    private static final double MAX_OWNER_TRACK_TIME = 100;
    private final Random random = new Random();
    private final Minecraft minecraft = Minecraft.getInstance();
    private final Map<Object, BoltOwnerData> boltOwners = new Object2ObjectOpenHashMap<>();
    private Timestamp refreshTimestamp = new Timestamp();

    public void render(float partialTicks, PoseStack pose, MultiBufferSource buffer) {
        if (this.minecraft.level == null) return;

        VertexConsumer consumer = buffer.getBuffer(JJKRenderTypes.lightning());
        Matrix4f matrix4f = pose.last().pose();
        Timestamp timestamp = new Timestamp(this.minecraft.level.getGameTime(), partialTicks);
        boolean refresh = timestamp.isPassed(this.refreshTimestamp, (1.0F / REFRESH_TIME));

        if (refresh) this.refreshTimestamp = timestamp;

        for (Iterator<Map.Entry<Object, BoltOwnerData>> iter = this.boltOwners.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<Object, BoltOwnerData> entry = iter.next();
            BoltOwnerData data = entry.getValue();

            // Tick our bolts based on the refresh rate, removing if they're now finished
            if (refresh) {
                data.bolts.removeIf(bolt -> bolt.tick(timestamp));
            }
            if (data.bolts.isEmpty() && data.lastBolt != null && data.lastBolt.getSpawnFunction().isConsecutive()) {
                data.addBolt(new BoltInstance(data.lastBolt, timestamp), timestamp);
            }
            data.bolts.forEach(bolt -> bolt.render(matrix4f, consumer, timestamp));

            if (data.bolts.isEmpty() && timestamp.isPassed(data.lastUpdateTimestamp, MAX_OWNER_TRACK_TIME)) {
                iter.remove();
            }
        }
    }

    public void update(Object owner, BoltEffect newBoltData, float partialTicks) {
        if (this.minecraft.level == null) return;

        BoltOwnerData data = this.boltOwners.computeIfAbsent(owner, ignored -> new BoltOwnerData());
        data.lastBolt = newBoltData;
        Timestamp timestamp = new Timestamp(this.minecraft.level.getGameTime(), partialTicks);

        if ((!data.lastBolt.getSpawnFunction().isConsecutive() || data.bolts.isEmpty()) && timestamp.isPassed(data.lastBoltTimestamp, data.lastBoltDelay)) {
            data.addBolt(new BoltInstance(newBoltData, timestamp), timestamp);
        }
        data.lastUpdateTimestamp = timestamp;
    }

    public static class BoltInstance {
        private final BoltEffect bolt;
        private final List<BoltEffect.BoltQuads> renderQuads;
        private final Timestamp createdTimestamp;

        public BoltInstance(BoltEffect bolt, Timestamp timestamp) {
            this.bolt = bolt;
            this.renderQuads = bolt.generate();
            this.createdTimestamp = timestamp;
        }

        public void render(Matrix4f matrix4f, VertexConsumer buffer, Timestamp timestamp) {
            float age = timestamp.subtract(this.createdTimestamp).value() / this.bolt.getLifespan();
            Pair<Integer, Integer> bounds = this.bolt.getFadeFunction().getRenderBounds(this.renderQuads.size(), age < 0.5F ? 2.0F * age : 2.0F * (1.0F - age));

            for (int i = bounds.getLeft(); i < bounds.getRight(); i++) {
                this.renderQuads.get(i).getVecs().forEach(v -> buffer.vertex(matrix4f, (float) v.x, (float) v.y, (float) v.z)
                        .color(this.bolt.getColor().x, this.bolt.getColor().y, bolt.getColor().z, this.bolt.getColor().w() * Math.min(1.0F, age + 0.5F))
                        .endVertex());
            }
        }

        public boolean tick(Timestamp timestamp) {
            return timestamp.isPassed(this.createdTimestamp, this.bolt.getLifespan());
        }
    }

    public static class Timestamp {
        private final long ticks;
        private final float partial;

        public Timestamp() {
            this(0, 0.0F);
        }

        public Timestamp(long ticks, float partial) {
            this.ticks = ticks;
            this.partial = partial;
        }

        public Timestamp subtract(Timestamp other) {
            long newTicks = this.ticks - other.ticks;
            float newPartial = this.partial - other.partial;

            if (newPartial < 0) {
                newPartial += 1;
                newTicks -= 1;
            }
            return new Timestamp(newTicks, newPartial);
        }

        public float value() {
            return this.ticks + this.partial;
        }

        public boolean isPassed(Timestamp prev, double duration) {
            long ticksPassed = this.ticks - prev.ticks;

            if (ticksPassed > duration) return true;

            duration -= ticksPassed;

            if (duration >= 1) return false;

            return (this.partial - prev.partial) >= duration;
        }
    }

    public class BoltOwnerData {
        private final Set<BoltInstance> bolts = new ObjectOpenHashSet<>();
        private BoltEffect lastBolt;
        private Timestamp lastBoltTimestamp = new Timestamp();
        private Timestamp lastUpdateTimestamp = new Timestamp();
        private double lastBoltDelay;

        private void addBolt(BoltInstance instance, Timestamp timestamp) {
            this.bolts.add(instance);
            this.lastBoltDelay = instance.bolt.getSpawnFunction().getSpawnDelay(BoltRenderer.this.random);
            this.lastBoltTimestamp = timestamp;
        }
    }
}