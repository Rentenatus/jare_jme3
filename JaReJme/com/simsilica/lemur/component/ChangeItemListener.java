/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simsilica.lemur.component;

import java.util.EventListener;

/**
 *
 * @author hpJanusch
 */
public interface ChangeItemListener extends EventListener {
    
    public void itemChanged(MenuItem item);
    
}
