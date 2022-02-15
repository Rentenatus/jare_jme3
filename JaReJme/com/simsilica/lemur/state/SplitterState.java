/*
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
