/********************************************************************************
 * jWebUnit, simplified web testing API for HttpUnit
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * 651 W Washington Ave. Suite 500
 * Chicago, IL 60661 USA
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     + Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     + Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     + Neither the name of ThoughtWorks, Inc., jWebUnit, nor the
 *       names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************/

package net.sourceforge.jwebunit.util.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wrapper to java reflection for method invocation.
 *
 * @author Wilkes Joiner
 */
public class MethodInvoker {
    private Object receiver;
    private String methodName;
    private Class[] argTypes;
    private Class receiverType;
    private Object[] args;

    public MethodInvoker(Object receiver, String methodName) {
        this(receiver, methodName, new Object[0]);
    }

    public MethodInvoker(Object receiver, String methodName, Object arg) {
        this(receiver, methodName, new Object[]{arg});
    }

    public MethodInvoker(Object receiver, String methodName, Object[] args) {
        this.receiver = receiver;
        receiverType = receiver.getClass();
        this.methodName = methodName;
        this.args = args;
        argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
    }

    public Method getMethod() throws NoSuchMethodException {
        try {
            return receiverType.getMethod(methodName, argTypes);
        } catch (NoSuchMethodException e) {
            convertToPrimitives();
            try {
                return receiverType.getMethod(methodName, argTypes);
            } catch (NoSuchMethodException e1) {
                String classes = "(";
                for (int i = 0; i < argTypes.length; i++) {
                    Class argType = argTypes[i];
                    classes += " " + argType.getName();
                    if (i != argTypes.length - 1) {
                        classes += ",";
                    }
                }
                classes += ")";
                throw new NoSuchMethodException(methodName + classes);
            }

        }
    }

    private void convertToPrimitives() {
        Class[] newArgTypes = new Class[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            Class argType = argTypes[i];
            if (argType.equals(Boolean.class)) {
                newArgTypes[i] = Boolean.TYPE;
            } else if (argType.equals(Byte.class)) {
                newArgTypes[i] = Byte.TYPE;
            } else if (argType.equals(Character.class)) {
                newArgTypes[i] = Character.TYPE;
            } else if (argType.equals(Double.class)) {
                newArgTypes[i] = Double.TYPE;
            } else if (argType.equals(Float.class)) {
                newArgTypes[i] = Float.TYPE;
            } else if (argType.equals(Integer.class)) {
                newArgTypes[i] = Integer.TYPE;
            } else if (argType.equals(Long.class)) {
                newArgTypes[i] = Long.TYPE;
            } else if (argType.equals(Short.class)) {
                newArgTypes[i] = Short.TYPE;
            } else {
                newArgTypes[i] = argType;
            }
        }
        argTypes = newArgTypes;
    }

    public Object invoke() throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        return getMethod().invoke(receiver, args);
    }
}
