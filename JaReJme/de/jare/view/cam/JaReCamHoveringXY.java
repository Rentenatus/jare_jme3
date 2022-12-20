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
public class JaReCamHoveringXY implements JaReCamSteering, JaReConst {

    private static final float MOVE_SPEED = 0.1f;

    private static final float ROTATION_SPEED = 5f;

    Vector3f trgPos;

    private float yaw;

    private float pitch;

    @Override
    public Vector3f zoomCamera(Camera cam, float value) {
        trgPos.setZ(trgPos.getZ() + value);
        checkLimits();
        Vector3f pos = trgPos.clone();
        return pos;
    }

    @Override
    public Vector3f moveCamera(Camera cam, float value, boolean sideways) { // Moving is strafing over the map plane
        Vector3f vel;

        if (sideways) {
            vel = new Vector3f(-value * MOVE_SPEED, 0, 0);
        } else {
            vel = new Vector3f(0, value * MOVE_SPEED, 0);
        }
        trgPos.addLocal(vel);

        Vector3f pos = trgPos.clone();
        return pos;
    }

    @Override
    public Vector3f gotoPos(Camera cam, Vector3f moveDir, Vector3f insensibility) {
        float dx = trgPos.getX() - moveDir.getX();
        float dy = trgPos.getY() - moveDir.getY();
        trgPos = new Vector3f(
                (dx < insensibility.getX() && dx > -insensibility.getX()) ? trgPos.getX() : moveDir.getX(),
                (dy < insensibility.getY() && dy > -insensibility.getY()) ? trgPos.getY() : moveDir.getY(),
                trgPos.getZ());
        return trgPos.clone();
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
        trgPos = cam.getLocation().clone();
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
        if (pitch > 1.9f) {
            pitch = 1.9f;
        } else if (pitch < -1.9f) {
            pitch = -1.9f;
        }
        float hight = trgPos.getZ();
        if (hight > 30.9f) {
            trgPos.setZ(30.9f);
        } else if (hight < 2.2f) {
            trgPos.setZ(2.2f);
        }
    }

    @Override
    public Vector3f getFocusDelta(Camera cam, float deep) {
        float distance = trgPos.getZ() - deep;
        float tanP = FastMath.tan(pitch) * distance;
        float tanY = FastMath.tan(yaw) * distance;
        return new Vector3f(tanY, tanP, 0);
    }

}
