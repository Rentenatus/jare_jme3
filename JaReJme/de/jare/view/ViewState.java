/*
 * Copyright (C) 2022 Janusch Rentenatus  
 */
package de.jare.view;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.lemur.SimJoystick;
import de.jare.view.cam.JaReCamControlSimpleXY;
import de.jare.basic.JaReConst;
import de.jare.basic.JaReKeybinding;

/**
 *
 * @author Janusch Rentenatus
 */
public class ViewState extends BaseAppState implements JaReConst {

    private JaReCamControl camControl;

    public ViewState(final AssetManager assetManager, final Camera camera) {
        super("JaReCam");
        init(assetManager, camera);
    }

    private void init(final AssetManager assetManager, final Camera camera) {
        this.camControl = new JaReCamControlSimpleXY(camera);
    }

    @Override
    protected void initialize(final Application app) {
        initMyKeys();
    }

    //private final ActionListener rotateListener = (name, keyPressed, tpf) -> {
    //    if (CAM_ROTATE.equals(name)) {
    //        if (keyPressed) {
    //            //???
    //        } else {
    //            //???
    //        }
    //    }
    //};
    //private final ActionListener markListener = (name, keyPressed, tpf) -> {
    //    if (MOUSE_BUTTON_LEFT.equals(name) && !keyPressed) {
    //        //???
    //    }
    //};
    //private final ActionListener cursorListener = (name, keyPressed, tpf) -> {
    //    //???
    //};
    /**
     * Custom Keybinding: Map named actions to inputs.
     */
    protected void initMyKeys() {
        final InputManager inputManager = getApplication().getInputManager();
        JaReKeybinding.initInputManager(inputManager);
        //inputManager.removeListener(rotateListener);
        //inputManager.removeListener(markListener);
        //inputManager.removeListener(cursorListener);

        // Add the names to the asction listener.
        //inputManager.addListener(rotateListener, CAM_ROTATE);
        //inputManager.addListener(markListener, MOUSE_BUTTON_LEFT);
        //inputManager.addListener(cursorListener, AXIS_LEFT, AXIS_RIGHT, AXIS_UP, AXIS_DOWN);
        camControl.initMyKeys(inputManager);
    }

    @Override
    public void update(final float tpf) {
        camControl.approachCamAxes();
        camControl.approachCampPos();
    }

    @Override
    protected void cleanup(final Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    public void moveCam(SimJoystick sjoy, float tpf, float maxWidth, float maxHeight) {
        Vector2f dir = sjoy.getDirection();
        if (dir.x != 0f || dir.y != 0f) {
            camControl.moveCam(dir.mult(tpf * 8f), maxWidth, maxHeight);
            sjoy.setSpeedShift(camControl.isSpeedShift());
        }
    }

    public boolean isSpeedShift() {
        return camControl.isSpeedShift();
    }

    public void gotoPos(Vector3f gotoPos, Vector3f insensibility) {
        camControl.gotoPos(gotoPos, insensibility);
    }

    public Camera getCamera() {
        return camControl.getCamera();
    }

    public Vector3f getCamLocation() {
        return camControl.getCamera().getLocation();
    }

}
