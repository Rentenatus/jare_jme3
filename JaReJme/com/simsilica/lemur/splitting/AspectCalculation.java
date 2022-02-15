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
