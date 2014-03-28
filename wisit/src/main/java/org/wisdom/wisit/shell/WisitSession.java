/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2013 - 2014 Wisdom Framework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.wisdom.wisit.shell;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Converter;
import org.wisdom.api.http.websockets.Publisher;

import java.io.PrintStream;

import static org.wisdom.wisit.shell.WisitOutputStream.OutputType;

/**
 * A Wrapper around a CommandSession in order to execute command from a web client and send back the result through
 * a web-socket.
 *
 * @author Jonathan M. Bardin
 */
public class WisitSession {

    /**
     * Gogo shell session
     */
    private final CommandSession shellSession;


    public WisitSession(final CommandProcessor processor,final Publisher publisher,final String topic) {
        WisitOutputStream resultStream = new WisitOutputStream(publisher,topic);
        WisitOutputStream errorStream = new WisitOutputStream(publisher,topic, OutputType.ERR);

        shellSession = processor.createSession(null, new PrintStream(resultStream,true),
                                                     new PrintStream(errorStream,true));
    }

    /**
     * Close the session.
     */
    public void close(){
        shellSession.close();
    }

    /**
     * Execute a command on the gogo shell.
     *
     * @param commandLine The command to be executed.
     * @return The CommandResult
     */
    public CommandResult exec(String commandLine) {
        CommandResult result = new CommandResult();

        try {
            Object raw = shellSession.execute(commandLine);

            if(raw != null){
                result.setResult(format(raw));
            }

        } catch (Exception e) {
            result.setErr(e.getMessage());
        }

        return result;
    }

    /**
     * Format the given object as a String
     * @param o The raw object to be formatted
     * @return The formatted string version of the raw object.
     */
    private String format(Object o) {
        return shellSession.format(o, Converter.INSPECT).toString();
    }
}
