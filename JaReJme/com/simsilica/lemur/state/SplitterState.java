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
package com.simsilica.lemur.state;

import com.jme3.app.Application;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import static de.jare.basic.JaReConst.MOUSE_BUTTON_LEFT;
import static de.jare.basic.JaReConst.MOUSE_MOVE;
import de.jare.basic.JaReKeybinding;
import com.simsilica.lemur.splitting.SplitterAlignment;

/**
 *
 * @author Janusch Rentenatus
 */
public class SplitterState extends SplitterTemplate {

    public SplitterState(String id, SplitterAlignment alignment,
            ColorRGBA background1, ColorRGBA background2, float aSplittPos) {
        super(id, alignment, background1, background2, aSplittPos);
    }

    private final AnalogListener mouseMoveListener = (name, keyPressed, tpf) -> {
        if (moving) {
            float calcPos = Math.max(0.02f, Math.min(0.98f, calcPos()));
            if (splittPos != calcPos) {
                waitingFrozen = 0f;
            }
            setPos(calcPos);
        }
    };

    private final ActionListener startMoveListener = (name, keyPressed, tpf) -> {
        if (MOUSE_BUTTON_LEFT.equals(name)) {
            if (keyPressed) {
                moving = isStartMove();
                waitingFrozen = 0f;
            } else if (moving) {
                resetViewports();
                moving = false;
            }
        }
    };

    @Override
    public void buildSplitterGeometry() {
        Application app = alignment.getApplication();
        Mesh mesh = alignment.createSplitterMesh();
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.05f, 0.02f, 0.24f, 0.62f));
        splitter = new Geometry("SplitterVBar", mesh);
        splitter.setMaterial(mat);
        alignment.getGuiNode().attachChild(splitter);
        setPos(splittPos);
    }

    @Override
    protected void initialize(Application arg0) {
        initMyKeys();
    }

    /**
     * Custom Keybinding: Map named actions to inputs.
     */
    private void initMyKeys() {
        final InputManager inputManager = getApplication().getInputManager();
        JaReKeybinding.initInputManager(inputManager);

        // Add the names to the asction listener.
        inputManager.addListener(mouseMoveListener, MOUSE_MOVE);
        inputManager.addListener(startMoveListener, MOUSE_BUTTON_LEFT);

    }

}
