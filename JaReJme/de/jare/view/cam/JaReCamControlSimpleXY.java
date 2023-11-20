/*
 * Copyright (C) 2022 Janusch Rentenatus  
 */
package de.jare.view.cam;

import de.jare.view.JaReCamControl;
import de.jare.view.JaReCamSteering;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.lemur.GuiGlobals;
import de.jare.basic.JaReConst;

/**
 *
 * @author Janusch Rentenatus  
 */
public class JaReCamControlSimpleXY implements JaReCamControl, JaReConst {

    public final static float FOLLOWSPEED = 0.25f;

    private boolean rotateCamera;
    private boolean speedShift;
    private boolean wasdMove;
    private final JaReCamSteering camSteering;
    private InputManager inputManager;
    private float followSpeed;
    private float moveZoomSpeed= 3f; 
    private float moveSlowSpeed= 2f;
    private float moveFastSpeed= 6f;
    private float rotateSlowSpeed= 1f;
    private float rotateFastSpeed= 2f;


    private final Camera cam;
    private Vector3f camPos;
    private Quaternion camAxes;

    public JaReCamControlSimpleXY(final Camera cam) {
        this.cam = cam;
        rotateCamera = false;
        speedShift = false;
        wasdMove = true;
        followSpeed = FOLLOWSPEED;
        camSteering = new JaReCamHoveringXY();
        camSteering.init(cam);
    }

    @Override
    public void setMoveZoomSpeed(float moveZoomSpeed) {
        this.moveZoomSpeed = moveZoomSpeed;
    }

    @Override
    public void setMoveSlowSpeed(float moveSlowSpeed) {
        this.moveSlowSpeed = moveSlowSpeed;
    }

    @Override
    public void setMoveFastSpeed(float moveFastSpeed) {
        this.moveFastSpeed = moveFastSpeed;
    }

    @Override
    public void setRotateSlowSpeed(float rotateSlowSpeed) {
        this.rotateSlowSpeed = rotateSlowSpeed;
    }

    @Override
    public void setRotateFastSpeed(float rotateFastSpeed) {
        this.rotateFastSpeed = rotateFastSpeed;
    }
    
 

    private final AnalogListener wasdListener = (name, value, tpf) -> {
        boolean noGuiFocus= GuiGlobals.getInstance().getFocusManagerState().getFocus()==null;
        if (noGuiFocus && wasdMove && null != name) switch (name) {
        case EGO_UP:
            moveCamera(1, false);
            break;
        case EGO_DOWN:
            moveCamera(-1, false);
            break;
        case EGO_RIGHT:
            moveCamera(-1, true);
            break;
        case EGO_LEFT:
            moveCamera(1, true);
            break;
        case CAM_RIGHT:
            roteteCamera(-tpf / 5, true);
            break;
        case CAM_LEFT:
            roteteCamera(tpf / 5, true);
            break;
        case ZOOM_IN:
            zoomCamera(tpf);
            break;
        case ZOOM_OUT:
            zoomCamera(-tpf);
            break;
        default:
            break;
        }
    };

    private final AnalogListener axisListener = (String name, float value, float tpf) -> {
        if (rotateCamera && null != name) switch (name) {
            case AXIS_LEFT:
                roteteCamera(value, true);
                break;
            case AXIS_RIGHT:
                roteteCamera(-value, true);
                break;
            case AXIS_UP:
                roteteCamera(-value, false);
                break;
            case AXIS_DOWN:
                roteteCamera(value, false);
                break;
            default:
                break;
        }
        if (AXIS_WHEEL_UP.equals(name)) {
                zoomCamera(value);
        } else if (AXIS_WHEEL_DOWN.equals(name)) {
                zoomCamera(-value);
        }
    };

    private final ActionListener rotateListener = (name, keyPressed, tpf) -> {
            setRotateCamera(keyPressed);
    };

    private final ActionListener speedListener = (name, keyPressed, tpf) -> {
            setSpeedShift(keyPressed);
    };

    protected void zoomCamera(final float value) {
        if (camSteering != null && cam != null) { 
            camPos = camSteering.zoomCamera(cam, speedShift ? value * moveZoomSpeed : value);
            approachCampPos();
        }
    }
   
    protected void moveCamera(final float value, final boolean sideways) {
        if (camSteering != null && cam != null) { 
            camPos = camSteering.moveCamera(cam, speedShift ? value * moveFastSpeed : value * moveSlowSpeed, sideways);
            approachCampPos();
        }
    }
  
    void roteteCamera(final float value, final boolean sideways) {
        if (camSteering != null && cam != null) { 
            camAxes = camSteering.rotateCamera(cam, speedShift ? value * rotateFastSpeed : value * rotateSlowSpeed, sideways);
            approachCamAxes();
        }
    }

    @Override
    public void approachCamAxes() {
        if (camAxes != null) {
            final Vector3f upDir = cam.getUp();
            final Vector3f leftDir = cam.getLeft();
            final Vector3f dir = cam.getDirection();
            final Quaternion q = new Quaternion();
            q.fromAxes(leftDir, upDir, dir);
            if (!q.isSimilar(camAxes, EPSILON)) {
                q.slerp(camAxes, 0.25f);
                cam.setAxes(q);
            }
        }
    }

    @Override
    public void approachCampPos() {
        if (camPos != null && !cam.getLocation().isSimilar(camPos, EPSILON)) {
            final Vector3f v = cam.getLocation().mult(1f - followSpeed);
            v.addLocal(camPos.mult(followSpeed));
            cam.setLocation(v);
            followSpeed = followSpeed * 0.96f + FOLLOWSPEED * 0.04f;
        }
    }

    /**
     * Custom Keybinding: Map named actions to inputs.
     *
     * @param inputManager
     */
    @Override
    public void initMyKeys(final InputManager inputManager) {
        this.inputManager = inputManager;
        inputManager.removeListener(rotateListener);
        inputManager.removeListener(speedListener);
        inputManager.removeListener(axisListener);
        inputManager.removeListener(wasdListener);

        // Add the names to the asction listener.
        inputManager.addListener(rotateListener, CAM_ROTATE);
        inputManager.addListener(speedListener, SPEED_SHIFT);
        inputManager.addListener(axisListener, AXIS_LEFT, AXIS_RIGHT, AXIS_UP, AXIS_DOWN, AXIS_WHEEL_UP,
                AXIS_WHEEL_DOWN);
        inputManager.addListener(wasdListener, EGO_LEFT, EGO_RIGHT, EGO_UP, EGO_DOWN, CAM_LEFT, CAM_RIGHT, ZOOM_IN,
                ZOOM_OUT);
    }

    protected void setRotateCamera(final boolean rotateCamera) {
        this.rotateCamera = rotateCamera;
        //setCursorEventsEnabled() will already turn on/off the mouse cursor
        //inputManager.setCursorVisible(!rotateCamera);
        GuiGlobals.getInstance().setCursorEventsEnabled(!rotateCamera);
    }

    protected void setSpeedShift(final boolean speedShift) {
        this.speedShift = speedShift;
    }

    @Override
    public boolean isSpeedShift() {
        return speedShift;
    }

    @Override
    public boolean isRotateCamera() {
        return rotateCamera;
    }

    public void setWasdMove(final boolean wasdMove) {
        this.wasdMove = wasdMove;
    }

    @Override
    public Camera getCamera() {
        return cam;
    }

 

    /**
     *
     * @param moveDir
     * @param maxWidth
     * @param maxHeight
     */
    @Override
    public void moveCam(Vector2f moveDir, float maxWidth, float maxHeight) {
        if (camSteering == null || cam == null) {
            return;
        }
        if (speedShift) {
            moveDir = moveDir.mult(3f);
        }
        camPos = camSteering.moveCamera(cam, -moveDir.x, true);
        camPos.setX(Math.max(Math.min(camPos.x, maxWidth), 0f));
        approachCampPos();
        camPos = camSteering.moveCamera(cam, moveDir.y, false);
        camPos.setY(Math.min(Math.max(camPos.y, -maxHeight), 0f));
        approachCampPos();

    }

    @Override
    public void gotoPos(Vector3f gotoPos, Vector3f insensibility) {
        if (camSteering == null || cam == null) {
            return;
        }
        followSpeed = FOLLOWSPEED / 13f;
        Vector3f moveDir = gotoPos.add(camSteering.getFocusDelta(cam, 0f));
        camPos = camSteering.gotoPos(cam, moveDir, insensibility);
        approachCampPos();
    }
}
