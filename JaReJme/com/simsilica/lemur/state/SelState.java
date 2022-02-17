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
import com.jme3.app.state.BaseAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.PopUpMenu;
import com.simsilica.lemur.SelTextField;
import com.simsilica.lemur.component.MenuItem;
import com.simsilica.lemur.event.DefaultMouseListener;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.event.SelTextCommand;
import com.simsilica.lemur.text.SelDocumentModel;
import java.util.Iterator;

/**
 *
 * @author Janusch Rentenatus
 */
public class SelState extends BaseAppState {

    private final Node rootNode;
    private final ViewPort view;
    private Vector3f popupRedyPoint;
    private int cBeginMark;
    private Vector3f contactMarkPoint;
    private SelTextField contactSelTF;

    public SelState(Application app, ViewPort view, Node rootNode) {
        this.rootNode = rootNode;
        this.view = view;
    }

    @Override
    protected void initialize(Application app) {
        popupRedyPoint = null;
        cBeginMark = -1;
        contactMarkPoint = Vector3f.ZERO;
    }

    public void addControlTo(SelTextField field) {
        field.getControl(MouseEventControl.class).addMouseListener(selTextFieldMouseListener);
    }
    private final SelTextFieldMouseListener selTextFieldMouseListener = new SelTextFieldMouseListener();

    private class SelTextFieldMouseListener extends DefaultMouseListener {

        @Override
        public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
            if (event.getButtonIndex() == 0) {
                if (markSelectedTextField(event.isPressed())) {
                    event.setConsumed();
                }
            }
            if (event.getButtonIndex() == 1) {
                if (menuSelectedTextField(event.isPressed())) {
                    event.setConsumed();
                }
            }
        }

        @Override
        public void mouseMoved(MouseMotionEvent event, Spatial target, Spatial capture) {
            if (markProgressSelectedTextField()) {
                event.setConsumed();
            }
        }

        @Override
        public void mouseEntered(MouseMotionEvent event, Spatial target, Spatial capture) {

        }

        @Override
        public void mouseExited(MouseMotionEvent event, Spatial target, Spatial capture) {
        }
    }

    protected boolean markSelectedTextField(boolean keyPressed) {
        CollisionResult coli = collisionResults();
        SelTextField selTF = coli == null ? null
                : (SelTextField) coli.getGeometry().getParent();
        boolean warwas = false;
        if (coli != null && selTF != null) {
            SelDocumentModel documentModel = selTF.getDocumentModel();
            if (keyPressed) {
                contactMarkPoint = coli.getContactPoint();
                contactSelTF = selTF;
                int[] xy = calcXY(selTF, contactMarkPoint);
                int[] lines = selTF.getTextlinesbyCoordinates(xy);
                warwas = (cBeginMark >= 0);
                cBeginMark = -1;
                if (null != documentModel.findCaratValue(lines)) {
                    cBeginMark = (int) documentModel.findCaratValue(lines);
                    documentModel.emptyAnchors();
                    documentModel.updateCarat(true, cBeginMark, true);
                    warwas = true;
                }
            } else if (cBeginMark >= 0) {
                Vector3f contactPoint = coli.getContactPoint();
                int[] xy = calcXY(selTF, contactPoint);
                int[] lines = selTF.getTextlinesbyCoordinates(xy);
                if (null != documentModel.findCaratValue(lines)) {
                    int cEndMark = (int) documentModel.findCaratValue(lines);
                    if (cEndMark > 0 && contactMarkPoint.distance(contactPoint) > 2f) {
                        if (cEndMark <= cBeginMark) {
                            cEndMark--;
                        } else if (cEndMark > cBeginMark) {
                            cBeginMark--;
                        }
                    }
                    documentModel.updateCarat(true, cEndMark, true);
                    documentModel.addTextselectArea(cBeginMark, cEndMark);
                    warwas = true;
                    contactMarkPoint = Vector3f.ZERO;
                    contactSelTF = null;
                } else {
                    contactSelTF = null;
                }

            }
            // set Focus if we dont have it
            if (selTF != GuiGlobals.getInstance().getFocusManagerState().getFocus()) {
                GuiGlobals.getInstance().getFocusManagerState().setFocus(selTF);
            }
        }
        return warwas;
    }

    protected boolean markProgressSelectedTextField() {
        CollisionResult coli = collisionResults();
        SelTextField selTF = coli == null ? null
                : (SelTextField) coli.getGeometry().getParent();
        boolean warwas = false;
        if (coli != null && selTF != null) {
            SelDocumentModel documentModel = selTF.getDocumentModel();
            if (cBeginMark >= 0 && contactSelTF == selTF && contactMarkPoint.getX() != 0 && contactMarkPoint.getY() != 0) {
                Vector3f contactPoint = coli.getContactPoint();
                int[] xy = calcXY(selTF, contactPoint);
                int[] lines = selTF.getTextlinesbyCoordinates(xy);
                int cBeginMarkLocal = cBeginMark;
                if (null != documentModel.findCaratValue(lines)) {
                    int cEndMark = (int) documentModel.findCaratValue(lines);
                    if (cEndMark > 0 && contactMarkPoint.distance(contactPoint) > 2f) {
                        if (cEndMark <= cBeginMarkLocal) {
                            cEndMark--;
                        } else if (cEndMark > cBeginMarkLocal) {
                            cBeginMarkLocal--;
                        }
                    }
                    documentModel.addTextselectArea(cBeginMarkLocal, cEndMark);
                    warwas = true;
                }
            }
        }
        return warwas;
    }

    protected int[] calcXY(Node n, Vector3f v) {
        Quaternion q = n.getWorldRotation();
        Matrix3f m = q.inverse().toRotationMatrix();
        Vector3f tr = n.getWorldTranslation();
        Vector3f p = (tr.add(m.mult(v.subtract(tr))));
        return new int[]{(int) (p.getX()), (int) (p.getY())};
    }

    protected boolean menuSelectedTextField(boolean keyPressed) {
        if (keyPressed) {
            CollisionResult coli = collisionResults();
            popupRedyPoint = ((coli == null) ? null : coli.getContactPoint());
        } else if (popupRedyPoint != null) {
            CollisionResult coli = collisionResults();
            if (coli != null) {
                Vector3f endPoint = coli.getContactPoint();
                SelTextField selTF = (SelTextField) coli.getGeometry().getParent();
                if (selTF != null && popupRedyPoint.distance(endPoint) < 9.9f) {
                    showMenu(selTF);
                }
            }
            popupRedyPoint = null;
        } else {
            return false;
        }
        return true;
    }

    public CollisionResult collisionResults() {
        Camera cam = view.getCamera();

        // 1. Reset results list.
        final CollisionResults results = new CollisionResults();

        // 2. Aim the ray from Mouse Pointer.
        final Vector2f click2d = getApplication().getInputManager().getCursorPosition().clone();
        final Vector3f click3d = cam.getWorldCoordinates(click2d, 0f).clone();
        final Vector3f dir = cam.getWorldCoordinates(click2d, 1f).subtractLocal(click3d).normalizeLocal();
        final Ray ray = new Ray(click3d, dir);
        rootNode.collideWith(ray, results);

        Iterator<CollisionResult> it = results.iterator();
        while (it.hasNext()) {
            CollisionResult coli = it.next();
            Node parent = coli.getGeometry().getParent();
            if (parent instanceof SelTextField) {
                return coli;
            }
        }
        return null;
    }

    public void showMenu(SelTextField selTF) {
        if (selTF == null) {
            return;
        }
        PopUpMenu pm = new PopUpMenu();
        addCustomItems(pm, selTF);
        addMenuCopyPaste(pm, selTF);
        Vector3f scW = selTF.getWorldScale();
        pm.show(getApplication(), (scW.getX() + scW.getY()) * 0.5f);
    }

    private void addCustomItems(PopUpMenu pm, SelTextField selTF) {
        for (MenuItem mi : selTF.getMenuList()) {
            pm.addMenuItem(mi);
        }
    }

    protected void addMenuCopyPaste(PopUpMenu pm, SelTextField selTF) {
        String text = selTF.getDocumentModel().getfulltext();
        boolean hasContent = text != null && !text.isEmpty();
        text = hasContent ? selTF.getDocumentModel().getselectedText() : null;
        boolean hasSelect = text != null && !text.isEmpty();
        MenuItem mi;
        mi = pm.addMenuItem("Select all", newSelectAllAction(selTF));
        mi.setEnabled(hasContent);
        pm.addMenuItem("Delete", newDeleteAction(selTF));
        mi = pm.addMenuItem("Cut", newCutAction(selTF));
        mi.setEnabled(hasSelect);
        mi = pm.addMenuItem("Copy", newCopyAction(selTF));
        mi.setEnabled(hasSelect);
        pm.addMenuItem("Paste", newPasteAction(selTF));
    }

    protected SelTextCommand newDeleteAction(SelTextField actionElement) {
        return (new SelTextCommand(actionElement) {
            @Override
            public void execute(Button arg0) {
                getActionElement().actionDelete();
                closePopupMenu();
            }
        });
    }

    protected SelTextCommand newCutAction(SelTextField actionElement) {
        return (new SelTextCommand(actionElement) {
            @Override
            public void execute(Button arg0) {
                getActionElement().actionCopyCut();
                closePopupMenu();
            }
        });
    }

    protected SelTextCommand newCopyAction(SelTextField actionElement) {
        return (new SelTextCommand(actionElement) {
            @Override
            public void execute(Button arg0) {
                getActionElement().actionCopySelect();
                closePopupMenu();
            }
        });
    }

    protected SelTextCommand newPasteAction(SelTextField actionElement) {
        return (new SelTextCommand(actionElement) {
            @Override
            public void execute(Button arg0) {
                getActionElement().actionPaste();
                closePopupMenu();
            }
        });
    }

    protected SelTextCommand newSelectAllAction(SelTextField actionElement) {
        return (new SelTextCommand(actionElement) {
            @Override
            public void execute(Button arg0) {
                getActionElement().actionSelectAll();
                closePopupMenu();
            }
        });
    }

    @Override
    protected void cleanup(Application app) {
        // noop
    }

    @Override
    protected void onEnable() {
        // noop
    }

    @Override
    protected void onDisable() {
        // noop
    }

}
