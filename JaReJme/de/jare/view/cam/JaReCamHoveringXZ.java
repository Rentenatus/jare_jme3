/*
 * Copyright (C) 2022 Janusch Rentenatus  
 */
package de.jare.view.cam;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import de.jare.basic.JaReConst;
import de.jare.view.JaReCamSteering;

/**
 *
 * @author Janusch Rentenatus  
 */
public class JaReCamHoveringXZ implements JaReCamSteering, JaReConst {

    private static final float MOVE_SPEED = 0.3f;

    private static final float ROTATION_SPEED = 5f;

    private float hight;

    private float yaw;

    private float pitch;

    @Override
    public Vector3f zoomCamera(Camera cam, float value) {
        final Vector3f pos = cam.getLocation().clone();
        hight += value;
        checkLimits();
        pos.setY(hight);
        return pos;
    }

    @Override
    public Vector3f moveCamera(Camera cam, float value, boolean sideways) {
        // Moving is strafing over the map plane
        Vector3f vel = new Vector3f();
        final Vector3f pos = cam.getLocation().clone();

        if (sideways) {
            cam.getLeft(vel);
        } else {
            cam.getLeft(vel);
            vel = new Vector3f(-vel.getZ(), 0, vel.getX());
            // cam.getDirection(vel);
        }
        vel.multLocal(value * MOVE_SPEED);
        pos.addLocal(vel);
        pos.setY(hight);
        return pos;
    }

    @Override
    public Vector3f gotoPos(Camera cam, Vector3f moveDir, Vector3f insensibility) {
        return moveDir;
    }

    @Override
    public void init(Camera cam) {
        final Vector3f upDir = cam.getUp();
        final Vector3f leftDir = cam.getLeft();
        final Vector3f dir = cam.getDirection();
        final Quaternion q = new Quaternion();
        q.fromAxes(leftDir, upDir, dir);
        q.normalizeLocal();
        float[] angles = q.toAngles(null);

        pitch = angles[0];
        yaw = angles[1];
        hight = cam.getLocation().getY();
        checkLimits();
    }

    @Override
    public Quaternion rotateCamera(Camera cam, float value, boolean sideways) {
        if (sideways) {
            yaw += ROTATION_SPEED * value;
        } else {
            pitch += ROTATION_SPEED * value;
        }
        return getCameraRotete(cam);
    }

    protected Quaternion getCameraRotete(Camera cam) {
        checkLimits();
        Quaternion roll = new Quaternion();
        roll.fromAngles(pitch, yaw, 0.0f);
        return roll;
    }

    private void checkLimits() {
        if (yaw > FastMath.TWO_PI) {
            yaw -= FastMath.TWO_PI;
        } else if (yaw < 0) {
            yaw += FastMath.TWO_PI;
        }
        if (pitch > 0.9f) {
            pitch = 0.9f;
        } else if (pitch < -0.2f) {
            pitch = -0.2f;
        }
        if (hight > 34.9f) {
            hight = 34.9f;
        } else if (hight < 0.2f) {
            hight = 0.2f;
        }
    }

    @Override
    public Vector3f getFocusDelta(Camera cam, float deep) {
        return cam.getLocation().clone();
    }
}
