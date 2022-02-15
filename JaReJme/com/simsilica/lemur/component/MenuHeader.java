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
package com.simsilica.lemur.component;

import com.jme3.app.Application;
import com.jme3.renderer.ViewPort;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.PopUpMenu;
import com.simsilica.lemur.event.MenuItemCommand;

/**
 *
 * @author hpJanusch
 */
public class MenuHeader {
    
    private final MenuItem headerItem;
    private PopUpMenu menu;
    private Application application;
    private ViewPort view;
    
    public MenuHeader(String itemText) {
        this.headerItem = new MenuItem(itemText, headerClickCommand());
        this.menu = new PopUpMenu();
    }
    
    public MenuHeader(String itemText, PopUpMenu subMenu) {
        this.headerItem = new MenuItem(itemText, headerClickCommand());
        this.menu = subMenu;
    }
    
    public MenuItemCommand headerClickCommand() {
        return (new MenuItemCommand() {
            private PopUpMenu myPopupMenu;
            
            @Override
            public void execute(Button source) {
                if (myPopupMenu != null && application != null && view != null) {
                    myPopupMenu.show(application, view, source, 6f);
                }
            }
            
            @Override
            public void setPopUpMenu(PopUpMenu pm) {
                myPopupMenu = pm;
            }
        });
    }
    
    public void setMenu(PopUpMenu menu) {
        this.menu = menu;
        if (headerItem.getClickCommand() != null) {
            headerItem.getClickCommand().setPopUpMenu(menu);
        }
    }
    
    public void setApplication(Application application) {
        this.application = application;
    }
    
    public void setView(ViewPort view) {
        this.view = view;
    }
    
    public MenuItem getHeaderItem() {
        return headerItem;
    }
    
    public PopUpMenu getMenu() {
        return menu;
    }
    
    public Application getApplication() {
        return application;
    }
    
}
