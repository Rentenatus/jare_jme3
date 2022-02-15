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

import com.simsilica.lemur.Button;
import com.simsilica.lemur.PopUpMenu;
import com.simsilica.lemur.event.MenuItemCommand;

/**
 *
 * @author hpJanusch
 */
public class MenuItem {

    public final static String USERDATA_P1 = "MenuItem.UserDataParam1";
    public final static String USERDATA_P2 = "MenuItem.UserDataParam2";

    private String itemText;
    private MenuItemCommand clickCommand;
    private boolean enabled;
    private String parameter1;
    private String parameter2;

    public MenuItem(String itemText) {
        this.itemText = itemText;
        this.clickCommand = printlnClickCommands();
        this.enabled = true;
    }

    public MenuItem(String itemText, MenuItemCommand clickCommand) {
        this.itemText = itemText;
        this.clickCommand = clickCommand;
        this.enabled = true;
    }

    public MenuItemCommand printlnClickCommands() {
        return (new MenuItemCommand() {
            @Override
            public void execute(Button source) {
                System.out.println(itemText + ": The world is yours.");
            }

            @Override
            public void setPopUpMenu(PopUpMenu pm) {
                //noop
            }
        });
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(ChangeItemListener listener, String itemText) {
        this.itemText = itemText;
        listener.itemChanged(this);
    }

    public void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public MenuItemCommand getClickCommand() {
        return clickCommand;
    }

    public void setClickCommand(ChangeItemListener listener, MenuItemCommand clickCommand) {
        this.clickCommand = clickCommand;
        listener.itemChanged(this);
    }

    /**
     * Getter: Additional parameter that can be found in the user data of the
     * button. Can be used if the action comand is to be evaluated.
     *
     * <code>
     * public MenuItemCommand newSomthingAction(TreeDataElement selectedElement) {
     *    return (new TreeCommand(selectedElement) {
     *
     * @Override public void execute(Button arg0) { String param1 =
     * arg0.getUserData(MenuItem.USERDATA_P1); doSomthing();  closePopupMenu();
     * } }); }</code>
     *
     * @return parameter1
     */
    public String getParameter1() {
        return parameter1;
    }

    /**
     * Setter: Additional parameter that can be found in the user data of the
     * button.Can be used if the action comand is to be evaluated.
     *
     * @param parameter1
     */
    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    /**
     * Getter: Additional parameter that can be found in the user data of the
     * button. Can be used if the action comand is to be evaluated.
     *
     * @return parameter2
     */
    public String getParameter2() {
        return parameter2;
    }

    /**
     * Setter: Additional parameter that can be found in the user data of the
     * button.Can be used if the action comand is to be evaluated.
     *
     * @param parameter2
     */
    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }
}
