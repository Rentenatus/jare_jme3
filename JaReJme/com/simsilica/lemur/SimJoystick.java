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
package com.simsilica.lemur;

import com.jme3.app.Application;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.JoyLayout;
import com.simsilica.lemur.event.MouseListener;
import com.simsilica.lemur.event.PopupState;

/**
 *
 * @author Janusch Rentenatus
 */
public class SimJoystick extends Container {

    private ColorRGBA colDir = new ColorRGBA(0.78f, 0.83f, 0.88f, 0.72f);
    private ColorRGBA colDot = new ColorRGBA(0.82f, 0.87f, 1f, 1f);
    private ColorRGBA colRed = new ColorRGBA(0.92f, 0.57f, 0.71f, 0.72f);
    private ColorRGBA colYellow = new ColorRGBA(0.92f, 0.97f, 0.21f, 1f);

    private boolean uptodate;
    private final MoveJoystick moveJoystick;
    private boolean moving;
    private IconComponent imDir;
    private IconComponent imDot;
    private boolean speedShift;

    public SimJoystick() {
        uptodate = false;
        moveJoystick = new MoveJoystick();
        moving = false;
        speedShift = false;
    }

    protected void rebuild() {
        clearChildren();
        JoyLayout layout = new JoyLayout();
        setLayout(layout);
        imDir = new IconComponent("Textures/joystick/directions.png");
        imDir.setColor(colDir);
        imDot = new IconComponent("Textures/joystick/dotdir.png");
        imDot.setColor(colDot);
        Panel dirPanel = layout.addChild(new Panel());
        Panel dotPanel = layout.addChild(new Panel());
        dirPanel.setBackground(imDir);
        dotPanel.setBackground(imDot);
        setBackground(null);
        moveJoystick.setDotPanel(dotPanel);
        removeMouseListener(moveJoystick);
        addMouseListener(moveJoystick);
        uptodate = true;
    }

    protected void show() {
        if (!uptodate) {
            rebuild();
        }
        PopupState popupState = GuiGlobals.getInstance().getPopupState();
        popupState.showPopup(this);
        moving = true;
    }

    public void close() {
        PopupState popupState = GuiGlobals.getInstance().getPopupState();
        moving = false;
        if (popupState.isPopup(this)) {
            popupState.closePopup(this);
        }
    }

    public void show(Application app) {
        show();
        final Vector2f click2d = app.getInputManager().getCursorPosition();
        Vector3f size = getPreferredSize();
        Vector3f trans = new Vector3f(
                click2d.x - size.x * 0.5f,
                click2d.y + size.x * 0.5f,
                102);
        moveJoystick.resetDirection(click2d.clone());
        setLocalTranslation(trans);
    }

    public void setSpeedShift(boolean speedShift) {
        if (speedShift == this.speedShift) {
            return;
        }
        if (speedShift) {
            imDir.setColor(colYellow);
            imDot.setColor(colRed);
        } else {
            imDir.setColor(colDir);
            imDot.setColor(colDot);
        }
        this.speedShift = speedShift;
    }

    private class MoveJoystick implements MouseListener {

        private Vector2f direction;
        private Vector2f fly;
        private Panel dotPanel;
        private Vector2f startPos;

        public MoveJoystick() {
            direction = Vector2f.ZERO.clone();
            startPos = Vector2f.ZERO.clone();
            fly = Vector2f.ZERO.clone();
            dotPanel = null;
        }

        public void resetDirection(Vector2f start) {
            direction = Vector2f.ZERO.clone();
            startPos = start;
            fly = Vector2f.ZERO.clone();
        }

        public void setDotPanel(Panel dp) {
            dotPanel = dp;
        }

        @Override
        public void mouseButtonEvent(MouseButtonEvent arg0, Spatial arg1, Spatial arg2) {
            if (!arg0.isPressed()) {
                close();
            }
        }

        @Override
        public void mouseEntered(MouseMotionEvent arg0, Spatial arg1, Spatial arg2) {
        }

        @Override
        public void mouseExited(MouseMotionEvent arg0, Spatial arg1, Spatial arg2) {
            if (moving) {
                // popupState.closePopup calls mouseExited too.
                fly = direction.clone();
            }
            close();
        }

        @Override
        public void mouseMoved(MouseMotionEvent arg0, Spatial arg1, Spatial arg2) {
            float d = startPos.distanceSquared(arg0.getX(), arg0.getY());
            Vector3f size = getSize();
            float f = Math.min(size.x * size.y * 0.25f, d);
            direction = new Vector2f(arg0.getX() - startPos.x, arg0.getY() - startPos.y);
            if (f != d) {
                direction.multLocal(FastMath.sqrt(f / d));
            }
            dotPanel.setLocalTranslation(direction.x, direction.y, 1f);
        }
    }

    /**
     * Regularly query this method from an update in order to realize the
     * movement in the state.
     *
     * @return direction
     */
    public Vector2f getDirection() {
        if (moving) {
            return moveJoystick.direction;
        } else {
            return moveJoystick.fly;
        }
    }
}
