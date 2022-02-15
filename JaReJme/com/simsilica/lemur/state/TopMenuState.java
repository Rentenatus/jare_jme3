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
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.MenuHeader;
import com.simsilica.lemur.component.MenuItem;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.core.GuiComponent;
import com.simsilica.lemur.event.MenuItemCommand;
import com.simsilica.lemur.splitting.AspectCalculation;
import com.simsilica.lemur.splitting.SplitterHorizontal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Janusch Rentenatus
 */
public class TopMenuState extends SplitterTemplate implements AspectCalculation {

    private final Container topContainer;
    private float mHeight;
    private List<MenuHeader> headerList;
    private boolean uptodate;

    public TopMenuState(Application app, ViewPort replacedViewPort, Node guiNode) {
        this(app, 1f, replacedViewPort, guiNode);
    }

    public TopMenuState(Application app, float scale, ViewPort replacedViewPort, Node guiNode) {
        super("TopMenu." + replacedViewPort.getName(),
                new SplitterHorizontal(app, replacedViewPort, guiNode),
                ColorRGBA.DarkGray,
                new ColorRGBA(0.10f, 0.09f, 0.24f, 1f),
                1f - 0.05f);
        getCamera2().setParallelProjection(true);
        setRelation2(app.getGuiViewPort().getCamera());
        mHeight = 0.05f;
        headerList = new ArrayList<>();
        uptodate = false;
        topContainer = new Container();
        topContainer.setLocalScale(scale);
    }

    @Override
    protected void initialize(Application app) {
        getRootNode2().attachChild(topContainer);
        addCollisionRoot(app, getView2());
    }

    public void addHeader(MenuHeader header) {
        headerList.add(header);
    }

    protected void rebuild() {
        topContainer.clearChildren();
        Insets3f insets3f = new Insets3f(
                topContainer.getWorldScale().getY() + 1.2f,
                topContainer.getWorldScale().getX(),
                topContainer.getWorldScale().getY() + 1.4f,
                topContainer.getWorldScale().getX());
        Container inner = topContainer.addChild(new Container(new BoxLayout(Axis.X, FillMode.None)));
        inner.setInsets(insets3f);

        for (MenuHeader headerItem : headerList) {
            headerItem.setApplication(getApplication());
            headerItem.setView(getView2());
            MenuItem menuItem = headerItem.getHeaderItem();
            MenuItemCommand clickCommand = menuItem.getClickCommand();
            if (clickCommand != null) {
                Button clickMe = inner.addChild(new Button(menuItem.getItemText()));
                clickMe.setUserData(MenuItem.USERDATA_P1, menuItem.getParameter1());
                clickMe.setUserData(MenuItem.USERDATA_P2, menuItem.getParameter2());
                clickCommand.setPopUpMenu(headerItem.getMenu());
                clickMe.addClickCommands(clickCommand);
                clickMe.setBackground(null);
                if (!menuItem.getEnabled()) {
                    clickMe.setEnabled(false);
                    clickMe.setColor(new ColorRGBA(1, 0.5f, 0.4f, 0.88f));
                }
                clickMe.setInsets(new Insets3f(0f, 4f, 0f, 4f));
            } else {
                Panel p = inner.addChild(new Panel(""));
                GuiComponent myBG = new QuadBackgroundComponent(new ColorRGBA(0.18f, 0.03f, 0.8f, 0.72f));
                p.setBackground(myBG.clone());
                p.setPreferredSize(new Vector3f(20f, 2f, 0f));

            }
        }

        GuiComponent myBG = new QuadBackgroundComponent(new ColorRGBA(0.08f, 0.03f, 0.08f, 0.72f));
        inner.setBackground(myBG.clone());
        myBG = new QuadBackgroundComponent(new ColorRGBA(0.18f, 0.03f, 0.8f, 0.72f));
        topContainer.setBackground(myBG.clone());
        uptodate = true;
    }

    public void repaint() {
        if (!uptodate) {
            rebuild();
        }
        int h = getApplication().getGuiViewPort().getCamera().getHeight();
        float d = alignment.getReplacedViewPort().getCamera().getViewPortTop() - alignment.getReplacedViewPort().getCamera().getViewPortBottom();
        float y = topContainer.getPreferredSize().getY() * getScale();
        setMenuHeight(y / (h * d));
        topContainer.setLocalTranslation(0, 0, 180f);
    }

    protected void setMenuHeight(float mHeight) {
        this.mHeight = mHeight;
        setPos(1f - mHeight);
        resetViewports();
    }

    public float getScale() {
        return FastMath.sqrt(topContainer.getLocalScale().getX() * topContainer.getLocalScale().getY());
    }

    @Override
    protected void resetViewports() {
        super.resetViewports();
        getCamera2().setLocation(new Vector3f(
                getCamera2().getFrustumRight(),
                getCamera2().getFrustumBottom(),
                200));
    }

    @Override
    protected void onEnable() {
        super.onDisable();
        getRootNode2().attachChild(topContainer);
        uptodate = false;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        topContainer.removeFromParent();
        uptodate = false;
    }

    @Override
    public void update(final float tpf) {
        if (!uptodate) {
            repaint();
        }
        super.update(tpf);
    }

    @Override
    public void buildSplitterGeometry() {
        // noop
    }

}
