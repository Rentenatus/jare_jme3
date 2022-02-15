/*
 * $Id$
 *
 * Copyright (c) 2022 Janusch Rentenatus
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
 * * Neither the name of 'JaRe' nor the names of its contributors
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
package com.simsilica.lemur.splitting;

import com.jme3.app.Application;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;

/**
 *
 * @author Janusch Rentenatus
 */
public class SplitterVertical extends SplitterAlignment {

    private float thickness;

    public SplitterVertical(Application app, ViewPort replacedViewPort, Node guiNode, Camera masterCam1, Camera masterCam2) {
        super(app, replacedViewPort, guiNode, masterCam1, masterCam2);
        thickness = 3f;
    }

    public SplitterVertical(Application app, ViewPort replacedViewPort, Node guiNode) {
        super(app, replacedViewPort, guiNode, replacedViewPort.getCamera(), replacedViewPort.getCamera());
        thickness = 3f;
    }

    @Override
    public void setViewPortSplit(float splittPos) {
        float x = x2 - x1;
        getCamera1().setViewPort(x1, x1 + x * splittPos, y1, y2);
        getCamera2().setViewPort(x1 + x * splittPos, x2, y1, y2);
        fixAspect(relation1, getCamera1());
        fixAspect(relation2, getCamera2());
    }

    @Override
    public void fixAspect(Camera relation, Camera cam) {
        if (!fixAspectR(relation, cam)) {
            if (!fixAspectV(cam)) {
                fixAspectH(cam);
            }
        }
    }

    @Override
    public Mesh createSplitterMesh() {
        int h = cameraGui.getHeight();
        float h1 = h * y1;
        float h2 = h * y2;

        float[] vertexbuffer = new float[]{ //
            -thickness, h1, 0, //
            thickness, h1, 0,//
            -thickness, h2, 0,//
            thickness, h1, 0,//
            thickness, h2, 0,//
            -thickness, h2, 0,//
        };
        final Mesh triMesh = new Mesh();
        triMesh.setMode(Mesh.Mode.Triangles);
        triMesh.setBuffer(VertexBuffer.Type.Position, 3, vertexbuffer);
        triMesh.updateBound();
        return triMesh;
    }

    @Override
    public boolean isStartMove(float splittPos) {
        if (isCursorInside()) {
            Application app = getApplication();
            final Vector2f click2d = app.getInputManager().getCursorPosition();
            float width = cameraGui.getWidth();
            float x = x2 - x1;
            float pos = width * (x1 + x * splittPos);
            return (click2d.x - thickness <= pos && click2d.x + thickness >= pos);
        }
        return false;
    }

    @Override
    public float calcPos() {
        Application app = getApplication();
        final Vector2f click2d = app.getInputManager().getCursorPosition();
        float width = cameraGui.getWidth();
        float x = x2 - x1;
        if (x < 0.000001f) {
            return x1;
        }
        return ((click2d.x / width) - x1) / x;
    }

    @Override
    public void setPos(Geometry splitter, float splittPos) {
        float width = cameraGui.getWidth();
        float x = x2 - x1;
        splitter.setLocalTranslation(width * (x1 + x * splittPos), 0, 98f);
    }

}
