/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simsilica.lemur.event;

import com.simsilica.lemur.PopUpMenu;
import com.simsilica.lemur.SelTextField;

/**
 *
 * @author Janusch Rentenatus
 */
public abstract class SelTextCommand implements MenuItemCommand {

    private SelTextField actionElement;
    private PopUpMenu pm;
    private String[] actionAttr;

    public SelTextCommand(SelTextField actionElement, String... actionAttr) {
        this.actionElement = actionElement;
        this.pm = null;
        this.actionAttr = actionAttr;
    }

    public SelTextCommand(SelTextField actionElement) {
        this.actionElement = actionElement;
        this.pm = null;
        this.actionAttr = null;
    }

    public SelTextCommand() {
        this.actionElement = null;
        this.pm = null;
        this.actionAttr = null;
    }

    public void closePopupMenu() {
        if (pm != null) {
            pm.close();
        }
        pm = null;
    }

    public SelTextField getActionElement() {
        return actionElement;
    }

    public String[] getActionAttr() {
        return actionAttr;
    }

    public void setSelTextField(SelTextField actionElement) {
        this.actionElement = actionElement;
    }

    @Override
    public void setPopUpMenu(PopUpMenu pm) {
        this.pm = pm;
    }

}
