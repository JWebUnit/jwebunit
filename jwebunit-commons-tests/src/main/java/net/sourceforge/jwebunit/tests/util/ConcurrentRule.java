/**
 * Copyright (c) 2010, JWebUnit team.
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

package net.sourceforge.jwebunit.tests.util;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.concurrent.CountDownLatch;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class ConcurrentRule implements MethodRule {

    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Concurrent concurrent = frameworkMethod.getAnnotation(Concurrent.class);
                if (concurrent == null)
                    statement.evaluate();
                else {
                    final String name = frameworkMethod.getName();
                    final Thread[] threads = new Thread[concurrent.value()];
                    final CountDownLatch go = new CountDownLatch(1);
                    final CountDownLatch finished = new CountDownLatch(threads.length);
                    final Throwable[] fThrown = new Throwable[threads.length];
                    for (int i = 0; i < threads.length; i++) {
                        threads[i] = new Thread(new MyIndexedRunnable(i) {

                            public void run() {
                                try {
                                    go.await();
                                    statement.evaluate();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                } catch (Throwable throwable) {
                                    fThrown[getIndex()] = throwable;
                                    if (throwable instanceof RuntimeException)
                                        throw (RuntimeException) throwable;
                                    if (throwable instanceof Error)
                                        throw (Error) throwable;
                                    RuntimeException r = new RuntimeException(throwable.getMessage(), throwable);
                                    r.setStackTrace(throwable.getStackTrace());
                                    throw r;
                                } finally {
                                    finished.countDown();
                                }
                            }
                        }, name + "-Thread-" + i);
                        threads[i].start();
                    }
                    go.countDown();
                    finished.await();
                    //Check exceptions
                    for (int i = 0; i < threads.length; i++) {
                        if (fThrown[i] != null) {
                            throw fThrown[i]; 
                        }
                    }
                }
            }
        };
    }
    
    private abstract class MyIndexedRunnable implements Runnable {
        
        private int index;
        
        public MyIndexedRunnable(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }
        
    }
}
