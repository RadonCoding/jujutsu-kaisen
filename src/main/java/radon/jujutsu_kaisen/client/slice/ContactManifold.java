package radon.jujutsu_kaisen.client.slice;

import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.util.MathUtil;

// https://github.com/Alcatergit/Hbm-s-Nuclear-Tech-GIT/blob/Custom-1.12.2/src/main/java/com/hbm/physics/ContactManifold.java
public class ContactManifold {
    public static final float CONTACT_BREAK = 0.02F;
    public static final float CONTACT_BREAK_SQ = CONTACT_BREAK * CONTACT_BREAK;

    public Contact[] contacts = new Contact[4];
    public int contactCount = 0;

    public ContactManifold() {}

    public void update() {
        for (int i = 0; i < this.contactCount; i++) {
            Contact contact = this.contacts[i];
            contact.globalA = contact.bodyA.localToGlobalPos(contact.localA);
            contact.globalB = contact.bodyB.localToGlobalPos(contact.localB);
            this.contacts[i].depth = (float) contact.globalA.subtract(contact.globalB).dot(contact.normal);
        }

        for (int i = 0; i < this.contactCount; i++) {
            Contact contact = this.contacts[i];

            if (contact.depth > CONTACT_BREAK) {
                this.removeContact(i);
                i--;
            } else {
                Vec3 proj = contact.globalA.subtract(contact.normal.scale(contact.depth));
                double orthoDistToB = proj.subtract(contact.globalB).lengthSqr();

                if (orthoDistToB > CONTACT_BREAK_SQ) {
                    this.removeContact(i);
                    i--;
                }
            }
        }
    }

    public void removeContact(int idx) {
        this.contacts[idx] = null;

        for (int i = idx; i < 3; i++) {
            this.contacts[i] = this.contacts[i + 1];
            this.contacts[i + 1] = null;
        }
        this.contactCount--;
    }

    public boolean addContact(Contact contact) {
        int idx = this.getContactIndex(contact);
        boolean replace = true;

        if (idx < 0) {
            if (this.contactCount < 4) {
                idx = this.contactCount;
                replace = false;
            } else {
                idx = this.getLeastRemoteIndex(contact);
            }
        }

        if (idx >= 0) {
            if (!replace) {
                this.contactCount++;
            }
            this.contacts[idx] = contact;
            return true;
        }
        return false;
    }

    public int getLeastRemoteIndex(Contact c) {
        float deepest = -Float.MAX_VALUE;
        int deepIdx = -1;

        for (int i = 0; i < this.contactCount; i++) {
            if (this.contacts[i].depth > deepest) {
                deepest = this.contacts[i].depth;
                deepIdx = i;
            }
        }

        double res0 = 0.0F, res1 = 0F, res2 = 0.0F, res3 = 0.0F;

        if (deepIdx != 0) {
            Vec3 a = c.localA.subtract(this.contacts[1].localA);
            Vec3 b = this.contacts[3].localA.subtract(this.contacts[2].localA);
            res0 = a.cross(b).lengthSqr();
        }
        if (deepIdx != 1) {
            Vec3 a = c.localA.subtract(this.contacts[0].localA);
            Vec3 b = this.contacts[3].localA.subtract(this.contacts[2].localA);
            res1 = a.cross(b).lengthSqr();
        }
        if (deepIdx != 2) {
            Vec3 a = c.localA.subtract(this.contacts[0].localA);
            Vec3 b = this.contacts[3].localA.subtract(this.contacts[1].localA);
            res2 = a.cross(b).lengthSqr();
        }
        if (deepIdx != 3) {
            Vec3 a = c.localA.subtract(this.contacts[0].localA);
            Vec3 b = this.contacts[2].localA.subtract(this.contacts[1].localA);
            res3 = a.cross(b).lengthSqr();
        }
        return MathUtil.absMaxIdx(res0, res1, res2, res3);
    }

    public int getContactIndex(Contact contact)  {
        int idx = -1;
        double shortestDist = CONTACT_BREAK_SQ;

        for (int i = 0; i < this.contactCount; i++) {
            double dist = this.contacts[i].localA.subtract(contact.localA).lengthSqr();

            if (dist < shortestDist) {
                shortestDist = dist;
                idx = i;
            }
        }
        return idx;
    }
}
