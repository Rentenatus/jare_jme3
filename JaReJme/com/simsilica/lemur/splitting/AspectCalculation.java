/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simsilica.lemur.splitting;

import com.jme3.renderer.Camera;

/**
 *
 * @author Janusch Rentenatus
 */
public interface AspectCalculation {

    public default boolean fixAspectH(Camera cam) {
        float h = cam.getHeight() * (cam.getViewPortTop() - cam.getViewPortBottom());
        float w = cam.getWidth() * (cam.getViewPortRight() - cam.getViewPortLeft());
        if (w > 0.0001f) {
            float aspectRatio = h / w;
            cam.setFrustumTop(cam.getFrustumRight() * aspectRatio);
            cam.setFrustumBottom(-cam.getFrustumTop());
            cam.onFrustumChange();
            return true;
        }
        return false;
    }

    public default boolean fixAspectV(Camera cam) {
        float h = cam.getHeight() * (cam.getViewPortTop() - cam.getViewPortBottom());
        float w = cam.getWidth() * (cam.getViewPortRight() - cam.getViewPortLeft());
        if (h > 0.0001f) {
            float aspectRatio = w / h;
            cam.setFrustumRight(cam.getFrustumTop() * aspectRatio);
            cam.setFrustumLeft(-cam.getFrustumRight());
            cam.onFrustumChange();
            return true;
        }
        return false;
    }

    public default boolean fixAspectR(Camera relation, Camera cam) {
        if (relation == null) {
            return false;
        }
        float halfHeight = relation.getHeight() * (cam.getViewPortTop() - cam.getViewPortBottom()) * 0.5f;
        float halfWidth = relation.getWidth() * (cam.getViewPortRight() - cam.getViewPortLeft()) * 0.5f;
        cam.setFrustumBottom(-halfHeight);
        cam.setFrustumLeft(-halfWidth);
        cam.setFrustumRight(halfWidth);
        cam.setFrustumTop(halfHeight);
        return true;
    }
}
