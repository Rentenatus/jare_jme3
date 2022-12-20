/*
 * Copyright (C) 2022 Janusch Rentenatus  
 */
package de.jare.view;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author Janusch Rentenatus  
 */
public interface JaReCamSteering {

    public Vector3f zoomCamera(Camera cam, float value);

    public Quaternion rotateCamera(Camera cam, final float value, final boolean sideways);

    public Vector3f moveCamera(Camera cam, final float value, final boolean sideways);

    public Vector3f gotoPos(Camera cam, Vector3f moveDir, Vector3f insensibility);

    public Vector3f getFocusDelta(Camera cam, float deep);

    public void init(Camera cam);

}
