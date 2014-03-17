/**
 * Copyright (c) 2002-2014, JWebUnit team.
 *
 * This file is part of JWebUnit.
 *
 * JWebUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JWebUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebUnit.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.jwebunit.tests.util.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wrapper to java reflection for method invocation.
 *
 * @author Wilkes Joiner
 */
public class StaticMethodInvoker {
    private String methodName;
    private Class<?>[] argTypes;
    private Class<?> receiverType;
    private Object[] args;

    public StaticMethodInvoker(Class<?> receiverType, String methodName) {
        this(receiverType, methodName, new Object[0]);
    }

    public StaticMethodInvoker(Class<?> receiverType, String methodName, Object arg) {
        this(receiverType, methodName, new Object[]{arg});
    }

    public StaticMethodInvoker(Class<?> receiverType, String methodName, Object[] args) {
        this.receiverType = receiverType;
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
                    Class<?> argType = argTypes[i];
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
        Class<?>[] newArgTypes = new Class[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            Class<?> argType = argTypes[i];
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
        return getMethod().invoke(null, args);
    }
}
