/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
