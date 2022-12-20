/*
 * Copyright (C) 2022 Janusch Rentenatus  
 */
package de.jare.basic;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

/**
 *
 * @author Janusch Rentenatus  
 */
public class JaReKeybinding implements JaReConst {

    /**
     * Custom Keybinding: Map named actions to inputs.
     *
     * @param inputManager
     */
    public static void initInputManager(final InputManager inputManager) {
        if (inputManager.hasMapping(CAM_ROTATE)) {
            return;
        }
        // You can map one or several inputs to one named action
        inputManager.addMapping(AXIS_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(AXIS_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(AXIS_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(AXIS_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(AXIS_WHEEL_UP, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping(AXIS_WHEEL_DOWN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping(EGO_LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(EGO_RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(EGO_UP, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(EGO_DOWN, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(CAM_LEFT, new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(CAM_RIGHT, new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(ZOOM_IN, new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping(ZOOM_OUT, new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping(CAM_ROTATE, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping(SPEED_SHIFT, new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping(MOUSE_BUTTON_LEFT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(POPUP_READY, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping(MOUSE_MOVE, new MouseAxisTrigger(MouseInput.AXIS_X, true), new MouseAxisTrigger(MouseInput.AXIS_X, false), new MouseAxisTrigger(MouseInput.AXIS_Y, false), new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        inputManager.addMapping(KEYS_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(KEYS_LEFT, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(KEYS_UP, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(KEYS_DOWN, new KeyTrigger(KeyInput.KEY_DOWN));

    }

}
