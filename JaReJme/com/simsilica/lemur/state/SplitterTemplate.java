/*
 */
package com.simsilica.lemur.state;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.simsilica.lemur.splitting.SplitterAlignment;

/**
 *
 * @author Janusch Rentenatus
 */
public abstract class SplitterTemplate extends BaseAppState implements LemurableView {

    protected final SplitterAlignment alignment;
    private final Node rootNode1;
    private final Camera camera1;
    private ViewPort view1;
    private final Node rootNode2;
    private final Camera camera2;
    private ViewPort view2;
    protected Geometry splitter;
    protected float splittPos;
    protected float waitingFrozen;
    protected boolean moving;

    public SplitterTemplate(String id, SplitterAlignment alignment,
            ColorRGBA background1, ColorRGBA background2, float aSplittPos) {
        super(id);
        this.alignment = alignment;
        Application app = alignment.getApplication();
        ViewPort replacedViewPort = alignment.getReplacedViewPort();
        replacedViewPort.setEnabled(false);
        moving = false;
        waitingFrozen = -1f;

        splittPos = aSplittPos;
        alignment.setViewPortSplit(splittPos);

        rootNode1 = new Node(id + ".SplitterRootNode1");

        camera1 = alignment.getCamera1();
        view1 = app.getRenderManager().createMainView(id + ".Splitter1", camera1);
        view1.setClearFlags(true, true, true);
        view1.attachScene(rootNode1);
        view1.setBackgroundColor(background1);

        rootNode2 = new Node(id + ".SplitterRootNode2");

        camera2 = alignment.getCamera2();
        view2 = app.getRenderManager().createMainView(id + ".Splitter2", camera2);
        view2.setClearFlags(true, true, true);
        view2.attachScene(rootNode2);
        view2.setBackgroundColor(background2);

        buildSplitterGeometry();
    }

    protected boolean isStartMove() {
        return alignment.isStartMove(splittPos);
    }

    protected boolean isCursorInside() {
        return alignment.isCursorInside();
    }

    protected float calcPos() {
        return alignment.calcPos();
    }

    protected void setPos(float newPos) {
        splittPos = newPos;
        alignment.setPos(splitter, splittPos);
    }

    protected void resetViewports() {
        alignment.setViewPortSplit(splittPos);
    }

    public void addCollisionRoot1(Application app) {
        addCollisionRoot(app, view1);
    }

    public void addCollisionRoot2(Application app) {
        addCollisionRoot(app, view2);
    }

    @Override
    protected void cleanup(Application app) {
        removeCollisionRoot(app, view1);
        removeCollisionRoot(app, view2);
        disposeViewPort();
    }

    @Override
    protected void onEnable() {
        // noop
    }

    @Override
    protected void onDisable() {
        if (splitter != null) {
            splitter.removeFromParent();
        }
    }

    @Override
    public void update(final float tpf) {
        super.update(tpf);
        if (alignment.replacedViewPortChanged()) {
            alignment.reshapeViewPort();
            if (splitter != null) {
                splitter.removeFromParent();
            }
            buildSplitterGeometry();
            resetViewports();
        } else if (moving) {
            waitingFrozen += tpf;
            if (waitingFrozen > 0.2f) {
                resetViewports();
                waitingFrozen = -10f;
            }
        }
        rootNode1.updateLogicalState(tpf);
        rootNode2.updateLogicalState(tpf);
    }

    @Override
    public void render(RenderManager rm) {
        super.render(rm);
        rootNode1.updateGeometricState();
        rootNode2.updateGeometricState();
    }

    protected void disposeViewPort() {
        if (view1 == null || view2 == null) {
            return;
        }
        getApplication().getRenderManager().removePostView(view1);
        getApplication().getRenderManager().removePostView(view2);
        view1 = view2 = null;
    }

    public Node getRootNode1() {
        return rootNode1;
    }

    public Camera getCamera1() {
        return camera1;
    }

    public ViewPort getView1() {
        return view1;
    }

    public Node getRootNode2() {
        return rootNode2;
    }

    public Camera getCamera2() {
        return camera2;
    }

    public ViewPort getView2() {
        return view2;
    }

    public void setRelation1(Camera relation1) {
        alignment.setRelation2(relation1);
    }

    public void setRelation2(Camera relation2) {
        alignment.setRelation2(relation2);
    }

    public abstract void buildSplitterGeometry();
}
