/**
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.environment.se;


/**
 * This is the main class that can be called from the command line for
 * a WeldContainer SE app which makes use of the ContainerInitialized event.
 * Something like:
 * <code>
 * java -cp weld-se.jar:my-app.jar org.jboss.weld.environment.se.StartMain arg1 arg2
 * </code>
 *
 * @author Peter Royle
 * @author Pete Muir
 * @author Ales Justin
 */
public class StartMain {
    public static String[] PARAMETERS;

    public StartMain(String[] commandLineArgs) {
        PARAMETERS = commandLineArgs;
    }

    private ShutdownHook shutdownHook;

    public WeldContainer go() {
        Weld weld = new Weld();
        shutdownHook = new ShutdownHook(weld);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        WeldContainer container = weld.initialize();
        return container;
    }

    /**
     * The main method called from the command line.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new StartMain(args).go();
    }

    public static String[] getParameters() {
        String[] copy = new String[PARAMETERS.length];
        System.arraycopy(PARAMETERS, 0, copy, 0, PARAMETERS.length);
        return copy;
    }

    /**
     * Shut down Weld immediately. Removes shutdown hook. Blocks until Weld is completely shut down.
     */
    public void shutdownNow() {
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
        shutdownHook.run();
    }

    static class ShutdownHook extends Thread {
        private final Weld weld;

        ShutdownHook(Weld weld) {
            this.weld = weld;
        }

        @Override
        public void run() {
            weld.shutdown();
        }
    }
}
