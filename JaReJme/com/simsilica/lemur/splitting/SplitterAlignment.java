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

/**
 *
 * @author Janusch Rentenatus
 */
public abstract class SplitterAlignment implements AspectCalculation{

    private final Application app;
    private final ViewPort replacedViewPort;
    private final Node guiNode;
    protected Camera relation1;
    private final Camera camera1;
    protected Camera relation2;
    private final Camera camera2;
    protected float x1;
    protected float x2;
    protected float y1;
    protected float y2;
    protected final Camera cameraGui;

    public SplitterAlignment(Application app, ViewPort replacedViewPort, Node guiNode, Camera masterCam1, Camera masterCam2) {
        this.app = app;
        this.replacedViewPort = replacedViewPort;
        this.guiNode = guiNode;
        camera1 = masterCam1.clone();
        camera2 = masterCam2.clone();
        cameraGui = app.getGuiViewPort().getCamera();
        Camera replacedCam = replacedViewPort.getCamera();
        x1 = replacedCam.getViewPortLeft();
        x2 = replacedCam.getViewPortRight();
        y1 = replacedCam.getViewPortBottom();
        y2 = replacedCam.getViewPortTop();
        relation1 = null;
        relation2 = null;
    }

    public Application getApplication() {
        return app;
    }

    public ViewPort getReplacedViewPort() {
        return replacedViewPort;
    }

    public Node getGuiNode() {
        return guiNode;
    }

    public Camera getCamera1() {
        return camera1;
    }

    public Camera getCamera2() {
        return camera2;
    }

    public void setRelation1(Camera relation1) {
        this.relation1 = relation1;
    }

    public void setRelation2(Camera relation2) {
        this.relation2 = relation2;
    }

    public abstract void setViewPortSplit(float splittPos);

    public abstract Mesh createSplitterMesh();

    public abstract boolean isStartMove(float splittPos);

    public boolean isCursorInside() {
        Application app = getApplication();
        final Vector2f click2d = app.getInputManager().getCursorPosition();
        float height = cameraGui.getHeight();
        float width = cameraGui.getWidth();
        return (click2d.getX() >= x1 * width)
                && (click2d.getX() <= x2 * width)
                && (click2d.getY() >= y1 * height)
                && (click2d.getY() <= y2 * height);
    }

    public abstract float calcPos();

    public abstract void setPos(Geometry splitter, float splittPos);

    public boolean replacedViewPortChanged() {
        Camera replacedCam = replacedViewPort.getCamera();
        return (x1 != replacedCam.getViewPortLeft()
                || x2 != replacedCam.getViewPortRight()
                || y1 != replacedCam.getViewPortBottom()
                || y2 != replacedCam.getViewPortTop());
    }

    public void reshapeViewPort() {
        Camera replacedCam = replacedViewPort.getCamera();
        x1 = replacedCam.getViewPortLeft();
        x2 = replacedCam.getViewPortRight();
        y1 = replacedCam.getViewPortBottom();
        y2 = replacedCam.getViewPortTop();
    }

    public abstract void fixAspect(Camera relation, Camera cam);

  

}
