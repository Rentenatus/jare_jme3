/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simsilica.lemur.event;

import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.PopUpMenu;

/**
 *
 * @author Janusch Rentenatus
 */
public interface MenuItemCommand extends Command<Button> {

    public void setPopUpMenu(PopUpMenu pm);

}
