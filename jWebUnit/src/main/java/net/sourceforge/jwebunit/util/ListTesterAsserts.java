/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
**********************************/
package net.sourceforge.jwebunit.util;

import net.sourceforge.jwebunit.WebTester;

import java.lang.reflect.Method;

public class ListTesterAsserts {
    public static void main(String[] args) {
        Method[] methods = WebTester.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().startsWith("assert")) {
                Class[] argTypes = method.getParameterTypes();
                String classes = "(";
                for (int j = 0; j < argTypes.length; j++) {
                    Class argType = argTypes[j];
                    classes += argType.getName();
                    if (j != argTypes.length - 1) {
                        classes += ", ";
                    }
                }
                classes += ")";
                System.out.println(method.getName() + classes);
            }
        }
    }
}
