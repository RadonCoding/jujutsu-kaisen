package radon.jujutsu_kaisen.client.slice;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.util.MathUtil;

// https://github.com/Alcatergit/Hbm-s-Nuclear-Tech-GIT/blob/Custom-1.12.2/src/main/java/com/hbm/physics/Contact.java
public class Contact {
	public RigidBody bodyA;
	public RigidBody bodyB;
	public Collider a;
	public Collider b;
	public Vec3 localA;
	public Vec3 localB;
	public Vec3 globalA;
	public Vec3 globalB;
	public Vec3 normal;
	public float depth;
	public Vec3 tangent;
	public Vec3 bitangent;

	public Vec3 rA;
	public Vec3 rB;

	public Jacobian normalContact;
	public Jacobian tangentContact;
	public Jacobian bitangentContact;

	public Contact(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b, GJK.GJKInfo info) {
		this.a = a;
		this.b = b;

		this.bodyA = bodyA == null ? RigidBody.DUMMY : bodyA;
		this.bodyB = bodyB == null ? RigidBody.DUMMY : bodyB;

		this.localA = this.bodyA.globalToLocalPos(info.contactPointA);
		this.localB = this.bodyB.globalToLocalPos(info.contactPointB);
		this.globalA = info.contactPointA;
		this.globalB = info.contactPointB;
		this.normal = info.normal;
		this.depth = info.depth;

		// https://box2d.org/posts/2014/02/computing-a-basis/
		if (Math.abs(this.normal.x) >= 0.57735D) {
			this.tangent = new Vec3(this.normal.y, -this.normal.x, 0.0D).normalize();
		} else {
			this.tangent = new Vec3(0.0D, this.normal.z, -this.normal.y).normalize();
		}
		this.bitangent = this.normal.cross(this.tangent);

		this.normalContact = new Jacobian(false);
		this.tangentContact = new Jacobian(true);
		this.bitangentContact = new Jacobian(true);
	}

	public void init(float dt) {
		this.rA = this.globalA.subtract(this.bodyA == RigidBody.DUMMY ? this.a.localCentroid : this.bodyA.globalCentroid);
		this.rB = this.globalB.subtract(this.bodyB == RigidBody.DUMMY ? this.b.localCentroid : this.bodyB.globalCentroid);

		this.normalContact.init(this, this.normal, dt);
		this.tangentContact.init(this, this.tangent, dt);
		this.bitangentContact.init(this, this.bitangent, dt);
	}

	public void solve() {
		this.normalContact.solve(this);
		this.tangentContact.solve(this);
		this.bitangentContact.solve(this);
	}

	public static class Jacobian {
		private final boolean tangent;

		private Vec3 jVa;
		private Vec3 jWa;
		private Vec3 jVb;
		private Vec3 jWb;

		private float bias;
		private double effectiveMass;
		private double totalLambda;

		public Jacobian(boolean tangent) {
			this.tangent = tangent;
		}

		public void init(Contact contact, Vec3 dir, float dt) {
			this.jVa = dir.reverse();
			this.jWa = contact.rA.cross(dir).reverse();
			this.jVb = dir;
			this.jWb = contact.rB.cross(dir);

			if (!this.tangent) {
				float closingVel = (float) contact.bodyA.linearVelocity.reverse()
						.subtract(contact.bodyA.angularVelocity.cross(contact.rA))
						.add(contact.bodyB.linearVelocity)
						.add(contact.bodyB.angularVelocity.cross(contact.rB))
						.dot(contact.normal);
				float restitution = contact.bodyA.restitution * contact.bodyB.restitution;

				float beta = 0.2F;
				float dslop = 0.0005F;
				float rslop = 0.5F;
				this.bias = -(beta / dt) * Math.max(contact.depth - dslop, 0.0F) + Math.max(restitution * closingVel - rslop, 0.0F);
			}

			this.effectiveMass =
					contact.bodyA.invMass
							+ this.jWa.dot(MathUtil.transform(this.jWa, contact.bodyA.invGlobalInertiaTensor))
							+ contact.bodyB.invMass
							+ this.jWb.dot(MathUtil.transform(this.jWb, contact.bodyB.invGlobalInertiaTensor));
			this.effectiveMass = 1.0D / this.effectiveMass;

			this.totalLambda = 0.0D;
		}

		public void solve(Contact contact) {
			double jv =
					this.jVa.dot(contact.bodyA.linearVelocity)
							+ this.jWa.dot(contact.bodyA.angularVelocity)
							+ this.jVb.dot(contact.bodyB.linearVelocity)
							+ this.jWb.dot(contact.bodyB.angularVelocity);
			double lambda = this.effectiveMass * (-(jv + this.bias));
			double oldTotalLambda = this.totalLambda;

			if (this.tangent) {
				float friction = contact.bodyA.friction * contact.bodyB.friction;
				double maxFriction = friction * contact.normalContact.totalLambda;
				this.totalLambda = Mth.clamp(this.totalLambda + lambda, -maxFriction, maxFriction);
			} else {
				this.totalLambda = Math.max(0.0D, oldTotalLambda + lambda);
			}
			lambda = this.totalLambda - oldTotalLambda;

			contact.bodyA.addLinearVelocity(this.jVa.scale(contact.bodyA.invMass * lambda));
			contact.bodyA.addAngularVelocity(MathUtil.transform(this.jWa, contact.bodyA.invGlobalInertiaTensor).scale(lambda).normalize());
			contact.bodyB.addLinearVelocity(this.jVb.scale(contact.bodyB.invMass * lambda));
			contact.bodyB.addAngularVelocity(MathUtil.transform(this.jWb, contact.bodyB.invGlobalInertiaTensor).scale(lambda).normalize());
		}
	}
}

