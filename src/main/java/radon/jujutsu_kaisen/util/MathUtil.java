package radon.jujutsu_kaisen.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class MathUtil {
    public static int absMaxIdx(double... numbers){
        int idx = 0;
        double max = -Double.MAX_VALUE;

        for(int i = 0; i < numbers.length; i ++){
            double num = Math.abs(numbers[i]);
            if (num > max){
                idx = i;
                max = num;
            }
        }
        return idx;
    }

    public static void matFromQuat(Matrix3f mat, Quaternionf quat) {
        mat.m00 = 1 - 2 * quat.y * quat.y - 2 * quat.z * quat.z;
        mat.m01 = 2 * quat.x * quat.y - 2 * quat.z * quat.w;
        mat.m02 = 2 * quat.x * quat.z + 2 * quat.y * quat.w;

        mat.m10 = 2 * quat.x * quat.y + 2 * quat.z * quat.w;
        mat.m11 = 1 - 2 * quat.x * quat.x - 2 * quat.z * quat.z;
        mat.m12 = 2 * quat.y * quat.z - 2 * quat.x * quat.w;

        mat.m20 = 2 * quat.x * quat.z - 2 * quat.y * quat.w;
        mat.m21 = 2 * quat.y * quat.z + 2 * quat.x * quat.w;
        mat.m22 = 1 - 2 * quat.x * quat.x - 2 * quat.y * quat.y;
    }

    public static void quatFromMat(Quaternionf quat, Matrix3f mat) {
        float s;
        float tr = mat.m00 + mat.m11 + mat.m22;

        if (tr >= 0.0F) {
            s = Math.sqrt(tr + 1.0F);
            quat.w = s * 0.5F;
            s = 0.5F / s;
            quat.x = (mat.m21 - mat.m12) * s;
            quat.y = (mat.m02 - mat.m20) * s;
            quat.z = (mat.m10 - mat.m01) * s;
        } else {
            float max = Math.max(Math.max(mat.m00, mat.m11), mat.m22);

            if (max == mat.m00) {
                s = Math.sqrt(mat.m00 - (mat.m11 + mat.m22) + 1.0F);
                quat.x = s * 0.5F;
                s = 0.5F / s;
                quat.y = (mat.m01 + mat.m10) * s;
                quat.z = (mat.m20 + mat.m02) * s;
                quat.w = (mat.m21 - mat.m12) * s;
            } else if (max == mat.m11) {
                s = Math.sqrt(mat.m11 - (mat.m22 + mat.m00) + 1.0F);
                quat.y = s * 0.5F;
                s = 0.5F / s;
                quat.z = (mat.m12 + mat.m21) * s;
                quat.x = (mat.m01 + mat.m10) * s;
                quat.w = (mat.m02 - mat.m20) * s;
            } else {
                s = Math.sqrt(mat.m22 - (mat.m00 + mat.m11) + 1.0F);
                quat.z = s * 0.5F;
                s = 0.5F / s;
                quat.x = (mat.m20 + mat.m02) * s;
                quat.y = (mat.m12 + mat.m21) * s;
                quat.w = (mat.m10 - mat.m01) * s;
            }
        }
    }

    // https://en.wikipedia.org/wiki/Outer_product
    public static Matrix3f outer(Vec3 a, Vec3 b) {
        return new Matrix3f(
                (float) (a.x * b.x), (float) (a.x * b.y), (float) (a.x * b.z),
                (float) (a.y * b.x), (float) (a.y * b.y), (float) (a.y * b.z),
                (float) (a.z * b.x), (float) (a.z * b.y), (float) (a.z * b.z)
        );
    }

    public static Vec3 transform(Vec3 vec, Matrix3f mat) {
        double x = mat.m00 * vec.x + mat.m01 * vec.y + mat.m02 * vec.z;
        double y = mat.m10 * vec.x + mat.m11 * vec.y + mat.m12 * vec.z;
        double z = mat.m20 * vec.x + mat.m21 * vec.y + mat.m22 * vec.z;
        return new Vec3(x, y, z);
    }

    public static Matrix4f inverse(Matrix4f mat) {
        float
                a00 = mat.m00(), a01 = mat.m01(), a02 = mat.m02(), a03 = mat.m03(),
                a10 = mat.m10(), a11 = mat.m11(), a12 = mat.m12(), a13 = mat.m13(),
                a20 = mat.m20(), a21 = mat.m21(), a22 = mat.m22(), a23 = mat.m23(),
                a30 = mat.m30(), a31 = mat.m31(), a32 = mat.m32(), a33 = mat.m33(),

                b00 = a00 * a11 - a01 * a10,
                b01 = a00 * a12 - a02 * a10,
                b02 = a00 * a13 - a03 * a10,
                b03 = a01 * a12 - a02 * a11,
                b04 = a01 * a13 - a03 * a11,
                b05 = a02 * a13 - a03 * a12,
                b06 = a20 * a31 - a21 * a30,
                b07 = a20 * a32 - a22 * a30,
                b08 = a20 * a33 - a23 * a30,
                b09 = a21 * a32 - a22 * a31,
                b10 = a21 * a33 - a23 * a31,
                b11 = a22 * a33 - a23 * a32,

                det = b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06;

        if (det == 0) throw new ArithmeticException("Matrix is singular and cannot be inverted.");

        det = 1.0F / det;

        Matrix4f inverse = new Matrix4f();
        inverse.m00((a11 * b11 - a12 * b10 + a13 * b09) * det);
        inverse.m01((a02 * b10 - a01 * b11 - a03 * b09) * det);
        inverse.m02((a31 * b05 - a32 * b04 + a33 * b03) * det);
        inverse.m03((a22 * b04 - a21 * b05 - a23 * b03) * det);
        inverse.m10((a12 * b08 - a10 * b11 - a13 * b07) * det);
        inverse.m11((a00 * b11 - a02 * b08 + a03 * b07) * det);
        inverse.m12((a32 * b02 - a30 * b05 - a33 * b01) * det);
        inverse.m13((a20 * b05 - a22 * b02 + a23 * b01) * det);
        inverse.m20((a10 * b10 - a11 * b08 + a13 * b06) * det);
        inverse.m21((a01 * b08 - a00 * b10 - a03 * b06) * det);
        inverse.m22((a30 * b04 - a31 * b02 + a33 * b00) * det);
        inverse.m23((a21 * b02 - a20 * b04 - a23 * b00) * det);
        inverse.m30((a11 * b07 - a10 * b09 - a12 * b06) * det);
        inverse.m31((a00 * b09 - a01 * b07 + a02 * b06) * det);
        inverse.m32((a31 * b01 - a30 * b03 - a32 * b00) * det);
        inverse.m33((a20 * b03 - a21 * b01 + a22 * b00) * det);

        return inverse;
    }
}
