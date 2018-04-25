/**j-Interop (Pure Java implementation of DCOM protocol)    
 * Copyright (C) 2011  Danny Tylman
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional, 
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY; 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */

package org.jinterop.dcom.impls.wmi.structures;

/**
 *
 * @author danny
 */
public class JICIMMethod {
    private final String name;
    private final JICIMObjectBlock inParams;
    private final JICIMObjectBlock outParams;

    public JICIMMethod(String methodName, JICIMObjectBlock inParams, JICIMObjectBlock outParams) {
        this.name = methodName;
        this.inParams = inParams;
        this.outParams = outParams;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the inParams
     */
    public JICIMObjectBlock getInParams() {
        return inParams;
    }

    /**
     * @return the outParams
     */
    public JICIMObjectBlock getOutParams() {
        return outParams;
    }
    
}
