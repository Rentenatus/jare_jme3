/*
 * Copyright (C) 2022 Janusch Rentenatus  
 */
package de.jare.view;

import com.jme3.input.InputManager;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author Janusch Rentenatus  
 */
public interface JaReCamControl {

    public static final float EPSILON = 0.02f;

    public Camera getCamera();

    public void initMyKeys(InputManager inputManager);

    public boolean isSpeedShift();

    public void approachCamAxes();

    public void approachCampPos();

    public boolean isRotateCamera();

    public void moveCam(Vector2f moveDir, float maxWidth, float maxHeight);

    public void gotoPos(Vector3f gotoPos, Vector3f insensibility);

}
