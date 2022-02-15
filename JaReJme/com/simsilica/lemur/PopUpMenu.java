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
package com.simsilica.lemur;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.simsilica.lemur.component.ChangeItemListener;
import com.simsilica.lemur.component.MenuItem;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.core.GuiComponent;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.event.MenuItemCommand;
import com.simsilica.lemur.event.PopupState;
import java.util.Iterator;

/**
 *
 * @author Janusch Rentenatus
 */
public class PopUpMenu extends Container implements ChangeItemListener {

    private VersionedList<MenuItem> menuList;
    private boolean uptodate;

    public PopUpMenu() {
        menuList = new VersionedList<>();
        uptodate = false;
    }

    public void addMenuItem(MenuItem item) {
        uptodate = false;
        menuList.add(item);
    }

    public MenuItem addMenuItem(String itemText) {
        uptodate = false;
        MenuItem mi = new MenuItem(itemText);
        menuList.add(mi);
        return mi;
    }

    public MenuItem addMenuItem(String itemText, MenuItemCommand clickCommand) {
        uptodate = false;
        MenuItem mi = new MenuItem(itemText, clickCommand);
        menuList.add(mi);
        return mi;
    }

    public Iterator<MenuItem> itemIterator() {
        return menuList.iterator();
    }

    public boolean removeMenuItem(MenuItem item) {
        uptodate = false;
        return menuList.remove(item);
    }

    @Override
    public void itemChanged(MenuItem item) {
        uptodate = false;
    }

    protected void rebuild() {
        clearChildren();
        Insets3f insets3f = new Insets3f(
                getWorldScale().getY() + 1.4f,
                getWorldScale().getX(),
                getWorldScale().getY() + 1.2f,
                getWorldScale().getX());
        Container inner = addChild(new Container());
        inner.setInsets(insets3f);

        for (MenuItem menuItem : menuList) {
            MenuItemCommand clickCommand = menuItem.getClickCommand();
            if (clickCommand != null) {
                Button clickMe = inner.addChild(new Button(menuItem.getItemText()));
                clickMe.setUserData(MenuItem.USERDATA_P1, menuItem.getParameter1());
                clickMe.setUserData(MenuItem.USERDATA_P2, menuItem.getParameter2());
                clickCommand.setPopUpMenu(this);
                clickMe.addClickCommands(clickCommand);
                clickMe.setBackground(null);
                if (!menuItem.getEnabled()) {
                    clickMe.setEnabled(false);
                    clickMe.setColor(new ColorRGBA(1, 0.5f, 0.4f, 0.88f));
                }
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
        setBackground(myBG.clone());
        uptodate = true;
    }

    protected void show() {
        if (!uptodate) {
            rebuild();   
        }
        PopupState popupState = GuiGlobals.getInstance().getPopupState();
        popupState.showPopup(this);
    }

    public void close() {
        PopupState popupState = GuiGlobals.getInstance().getPopupState();
        popupState.closePopup(this);
    }

    public void show(Application app, float scale) {
        show();

        final Vector2f click2d = app.getInputManager().getCursorPosition();
        float width = app.getGuiViewPort().getCamera().getWidth();

        Vector3f size = getPreferredSize();

        setLocalScale(scale);
        setLocalTranslation(
                Math.min(click2d.x, width - size.x * scale),
                Math.max(click2d.y, size.y * scale),
                100);
    }

    public void show(Application app, ViewPort view, Panel panel, float inset) {
        show();

        float width = app.getGuiViewPort().getCamera().getWidth();
        float height = app.getGuiViewPort().getCamera().getHeight();
        float offX = view.getCamera().getViewPortLeft() * width;
        float offY = view.getCamera().getViewPortBottom() * height;

        Vector3f size = getPreferredSize();

        float scale = FastMath.sqrt(panel.getWorldScale().getX() * panel.getWorldScale().getY());
        setLocalScale(scale);
        Vector3f panelPos = panel.getWorldTranslation();
        Vector3f panelSize = panel.getSize();
        setLocalTranslation(
                Math.min(offX + panelPos.x, width - size.x * scale),
                Math.max(offY + inset, size.y * scale),
                100);
    }

}
