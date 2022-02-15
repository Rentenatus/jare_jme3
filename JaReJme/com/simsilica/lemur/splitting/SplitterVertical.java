/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
