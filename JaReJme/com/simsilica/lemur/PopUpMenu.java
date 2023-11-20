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

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.component.ChangeItemListener;
import com.simsilica.lemur.component.MenuItem;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.core.GuiComponent;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.event.MenuItemCommand;
import java.util.Iterator;

/**
 *
 * @author Janusch Rentenatus
 */
public class PopUpMenu extends PopUp implements ChangeItemListener {

    private VersionedList<MenuItem> menuList;

    public PopUpMenu() {
        super();
        menuList = new VersionedList<>();
    }

    public void addMenuItem(MenuItem item) {
        menuList.add(item);
        itemChanged(item);
    }

    public MenuItem addMenuItem(String itemText) {
        MenuItem mi = new MenuItem(itemText, "");
        menuList.add(mi);
        itemChanged(mi);
        return mi;
    }

    public MenuItem addMenuItem(String itemText, String itemHotKey) {
        MenuItem mi = new MenuItem(itemText, itemHotKey);
        menuList.add(mi);
        itemChanged(mi);
        return mi;
    }

    public MenuItem addMenuItem(String itemText, MenuItemCommand clickCommand) {
        MenuItem mi = new MenuItem(itemText, "", clickCommand);
        menuList.add(mi);
        itemChanged(mi);
        return mi;
    }

    public MenuItem addMenuItem(String itemText, String itemHotKey, MenuItemCommand clickCommand) {
        MenuItem mi = new MenuItem(itemText, itemHotKey, clickCommand);
        menuList.add(mi);
        itemChanged(mi);
        return mi;
    }

    public Iterator<MenuItem> itemIterator() {
        return menuList.iterator();
    }

    public boolean removeMenuItem(MenuItem item) {
        itemChanged(item);
        return menuList.remove(item);
    }

    @Override
    protected void rebuild() {
        clearChildren();
        Insets3f insets3f = new Insets3f(
                getWorldScale().getY() + 1.4f,
                getWorldScale().getX(),
                getWorldScale().getY() + 1.2f,
                getWorldScale().getX());
        Container inner = addChild(new Container());
        //inner.setLayout(new BorderLayout());
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
            if (menuItem.getItemText() != null && !menuItem.getItemText().isEmpty()
                    && menuItem.getItemHotKey() != null && !menuItem.getItemHotKey().isEmpty()) {
                inner.addChild(new Label(menuItem.getItemHotKey()), 2);
            }
        }
        GuiComponent myBG = new QuadBackgroundComponent(new ColorRGBA(0.08f, 0.03f, 0.08f, 0.72f));
        inner.setBackground(myBG.clone());
        myBG = new QuadBackgroundComponent(new ColorRGBA(0.18f, 0.03f, 0.8f, 0.72f));
        setBackground(myBG.clone());
        readyUpdated();
    }

}
