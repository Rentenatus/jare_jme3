/*
 * Copyright (C) 2022 Janusch Rentenatus  
 */
package com.simsilica.lemur.event;

import com.simsilica.lemur.PopUpMenu;
import com.simsilica.lemur.event.MenuItemCommand;

/**
 *
 * @author Janusch Rentenatus
 */
public abstract class JaReMenuItemCommand implements MenuItemCommand {

    private PopUpMenu pm;
    private int position;

    public JaReMenuItemCommand() {
        this.pm = null;
        this.position = -1;
    }

    public JaReMenuItemCommand(int position) {
        this.pm = null;
        this.position = position;
    }

    public void closePopupMenu() {
        if (pm != null) {
            pm.close();
        }
    }

    @Override
    public void setPopUpMenu(PopUpMenu pm) {
        this.pm = pm;
    }

    public int getPosition() {
        return position;
    }

}
