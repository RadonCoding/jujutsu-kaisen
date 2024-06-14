package radon.jujutsu_kaisen.util;

import org.joml.Matrix4f;

public class MathUtil {
    public static Matrix4f inverse(Matrix4f m) {
        float
                a00 = m.m00(), a01 = m.m01(), a02 = m.m02(), a03 = m.m03(),
                a10 = m.m10(), a11 = m.m11(), a12 = m.m12(), a13 = m.m13(),
                a20 = m.m20(), a21 = m.m21(), a22 = m.m22(), a23 = m.m23(),
                a30 = m.m30(), a31 = m.m31(), a32 = m.m32(), a33 = m.m33(),

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

        if (det == 0) {
            throw new ArithmeticException("Matrix is singular and cannot be inverted.");
        }

        float invDet = 1.0F / det;

        Matrix4f result = new Matrix4f();
        result.m00((a11 * b11 - a12 * b10 + a13 * b09) * invDet);
        result.m01((a02 * b10 - a01 * b11 - a03 * b09) * invDet);
        result.m02((a31 * b05 - a32 * b04 + a33 * b03) * invDet);
        result.m03((a22 * b04 - a21 * b05 - a23 * b03) * invDet);
        result.m10((a12 * b08 - a10 * b11 - a13 * b07) * invDet);
        result.m11((a00 * b11 - a02 * b08 + a03 * b07) * invDet);
        result.m12((a32 * b02 - a30 * b05 - a33 * b01) * invDet);
        result.m13((a20 * b05 - a22 * b02 + a23 * b01) * invDet);
        result.m20((a10 * b10 - a11 * b08 + a13 * b06) * invDet);
        result.m21((a01 * b08 - a00 * b10 - a03 * b06) * invDet);
        result.m22((a30 * b04 - a31 * b02 + a33 * b00) * invDet);
        result.m23((a21 * b02 - a20 * b04 - a23 * b00) * invDet);
        result.m30((a11 * b07 - a10 * b09 - a12 * b06) * invDet);
        result.m31((a00 * b09 - a01 * b07 + a02 * b06) * invDet);
        result.m32((a31 * b01 - a30 * b03 - a32 * b00) * invDet);
        result.m33((a20 * b03 - a21 * b01 + a22 * b00) * invDet);

        return result;
    }
}
