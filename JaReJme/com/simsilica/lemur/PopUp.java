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
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.simsilica.lemur.component.ChangeItemListener;
import com.simsilica.lemur.component.MenuItem;
import com.simsilica.lemur.event.PopupState;

/**
 *
 * @author Janusch Rentenatus
 */
public abstract class PopUp extends Container implements ChangeItemListener {

    private boolean uptodate;

    public PopUp() {
        uptodate = false;
    }

    @Override
    public void itemChanged(MenuItem item) {
        uptodate = false;
    }

    protected void readyUpdated() {
        uptodate = true;
    }

    protected abstract void rebuild();

    protected void show() {
        if (!uptodate) {
            rebuild();
        }
        PopupState popupState = GuiGlobals.getInstance().getPopupState();
        popupState.showPopup(this);
    }

    public void close() {
        PopupState popupState = GuiGlobals.getInstance().getPopupState();
        popupState.closePopup(this);
    }

    public void show(Application app, float scale) {
        show();

        final Vector2f click2d = app.getInputManager().getCursorPosition();
        float width = app.getGuiViewPort().getCamera().getWidth();

        Vector3f size = getPreferredSize();

        setLocalScale(scale);
        setLocalTranslation(
                Math.min(click2d.x, width - size.x * scale),
                Math.max(click2d.y, size.y * scale),
                100);
    }

    public void show(Application app, ViewPort view, Panel panel, float inset) {
        show();

        float width = app.getGuiViewPort().getCamera().getWidth();
        float height = app.getGuiViewPort().getCamera().getHeight();
        float offX = view.getCamera().getViewPortLeft() * width;
        float offY = view.getCamera().getViewPortBottom() * height;

        Vector3f size = getPreferredSize();

        float scale = FastMath.sqrt(panel.getWorldScale().getX() * panel.getWorldScale().getY());
        setLocalScale(scale);
        Vector3f panelPos = panel.getWorldTranslation();
        Vector3f panelSize = panel.getSize();
        setLocalTranslation(
                Math.min(offX + panelPos.x, width - size.x * scale),
                Math.max(offY + inset, size.y * scale),
                100);
    }

}
