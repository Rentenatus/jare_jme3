/*
 * Copyright (c) 2009-2019 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.scene.shape;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import java.io.IOException;

/**
 * <code>QuadGradient</code> represents a rectangular plane in space defined by
 * 4 vertices. The quad's lower-left side is contained at the local space origin
 * (0, 0, 0), while the upper-right side is located at the width/height
 * coordinates (width, height, 0).
 *
 * @author Kirill Vainer
 * @author Janusch Rentenatus
 */
public class QuadGradient extends Mesh {

    private float width;
    private float height;
    private Vector4f col1;
    private Vector4f col2;
    private Vector4f col3;
    private Vector4f col4;

    /**
     * Serialization only. Do not use.
     */
    protected QuadGradient() {
    }

    /**
     * Create a quad with the given width and height.The quad is always created
     * in the XY plane.
     *
     * @param width The X extent or width
     * @param height The Y extent or width
     * @param col RGBA
     */
    public QuadGradient(float width, float height, Vector4f col) {
        updateGeometry(width, height, col, col, col, col);
    }

    /**
     * Create a quad with the given width and height.The quad is always created
     * in the XY plane.
     *
     * @param width The X extent or width
     * @param height The Y extent or width
     * @param col1 RGBA
     * @param col2 RGBA
     * @param col3 RGBA
     * @param col4 RGBA
     */
    public QuadGradient(float width, float height, Vector4f col1, Vector4f col2, Vector4f col3, Vector4f col4) {
        updateGeometry(width, height, col1, col2, col3, col4);
    }

    /**
     * Create a quad with the given width and height.The quad is always created
     * in the XY plane.
     *
     * @param width The X extent or width
     * @param height The Y extent or width
     * @param rgba1 RGBA
     * @param rgba2 RGBA
     * @param rgba3 RGBA
     * @param rgba4 RGBA
     */
    public QuadGradient(float width, float height, ColorRGBA rgba1, ColorRGBA rgba2, ColorRGBA rgba3, ColorRGBA rgba4) {
        updateGeometry(width, height, rgba1.toVector4f(), rgba2.toVector4f(), rgba3.toVector4f(), rgba4.toVector4f());
    }

    /**
     * Create a quad with the given width and height.The quad is always created
     * in the XY plane.
     *
     * @param width The X extent or width
     * @param height The Y extent or width
     * @param flipCoords If true, the texture coordinates will be flipped along
     * the Y axis.
     * @param col RGBA
     */
    public QuadGradient(float width, float height, boolean flipCoords, Vector4f col) {
        updateGeometry(width, height, flipCoords, col, col, col, col);
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public Vector4f getCol1() {
        return col1;
    }

    public Vector4f getCol2() {
        return col2;
    }

    public Vector4f getCol3() {
        return col3;
    }

    public Vector4f getCol4() {
        return col4;
    }

    public void updateGeometry(float width, float height) {
        updateGeometry(width, height, false, col1, col2, col3, col4);
    }

    public void updateGeometry(float width, float height, Vector4f col1, Vector4f col2, Vector4f col3, Vector4f col4) {
        updateGeometry(width, height, false, col1, col2, col3, col4);
    }

    public void updateGeometry(Vector4f col1, Vector4f col2, Vector4f col3, Vector4f col4) {
        updateGeometry(width, height, false, col1, col2, col3, col4);
    }

    public void updateGeometry(float width, float height, boolean flipCoords,
            Vector4f col1, Vector4f col2, Vector4f col3, Vector4f col4) {
        this.width = width;
        this.height = height;
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
        setBuffer(Type.Position, 3, new float[]{0, 0, 0,
            width, 0, 0,
            width, height, 0,
            0, height, 0
        });

        if (flipCoords) {
            setBuffer(Type.TexCoord, 2, new float[]{0, 1,
                1, 1,
                1, 0,
                0, 0});
        } else {
            setBuffer(Type.TexCoord, 2, new float[]{0, 0,
                1, 0,
                1, 1,
                0, 1});
        }
        setBuffer(Type.Normal, 3, new float[]{0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1});
        if (height < 0) {
            setBuffer(Type.Index, 3, new short[]{0, 2, 1,
                0, 3, 2});
        } else {
            setBuffer(Type.Index, 3, new short[]{0, 1, 2,
                0, 2, 3});
        }

        setBuffer(Type.Color, 4, new float[]{
            col1.getX(), col1.getY(), col1.getZ(), col1.getW(), // A
            col2.getX(), col2.getY(), col2.getZ(), col2.getW(), // B
            col3.getX(), col3.getY(), col3.getZ(), col3.getW(), // C
            col4.getX(), col4.getY(), col4.getZ(), col4.getW() // D
        });

        updateBound();
        setStatic();
    }

    @Override
    public void read(JmeImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        width = capsule.readFloat("width", 0);
        height = capsule.readFloat("height", 0);
        float[] white = new float[]{1f, 1f, 1f, 1f};
        float[] col = capsule.readFloatArray("col1", white);
        if (col.length > 3) {
            col1 = new Vector4f(col[0], col[1], col[2], col[3]);
        }
        col = capsule.readFloatArray("col2", white);
        if (col.length > 3) {
            col2 = new Vector4f(col[0], col[1], col[2], col[3]);
        }
        col = capsule.readFloatArray("col3", white);
        if (col.length > 3) {
            col3 = new Vector4f(col[0], col[1], col[2], col[3]);
        }
        col = capsule.readFloatArray("col4", white);
        if (col.length > 3) {
            col4 = new Vector4f(col[0], col[1], col[2], col[3]);
        }

    }

    @Override
    public void write(JmeExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(width, "width", 0);
        capsule.write(height, "height", 0);
        float[] white = new float[]{1f, 1f, 1f, 1f};
        capsule.write(col1.toArray(null), "col1", white);
        capsule.write(col2.toArray(null), "col2", white);
        capsule.write(col3.toArray(null), "col3", white);
        capsule.write(col4.toArray(null), "col4", white);
    }
}
