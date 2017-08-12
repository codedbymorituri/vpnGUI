/*
vpnGUI - a basic front end for openVPN.
Written as part of a coding project for a Raspberry Pi.
Copyright (C) 2016 morituri
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see http://www.gnu.org/licenses.
 */

package vpnGUI;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class RunAsSuperUser {

    private final GUI updateGUI;
    public RunAsSuperUser(GUI guiForm) {
        updateGUI = guiForm;                                
    }

    public void SudoCommand(String arguments) {
        try {
            String pbString = "/usr/bin/sudo -S " + arguments + " 2>&1";
            ProcessBuilder pb = new ProcessBuilder(new String[]{"/bin/bash", "-c", pbString});
            Process p = pb.start();
            OutputStreamWriter sudoOutput = new OutputStreamWriter(p.getOutputStream());
            InputStreamReader sudoInput = new InputStreamReader(p.getInputStream());
            int bytes = 0;
            int pwTries = 0;
            char buffer[] = new char[1024];
            while ((bytes = sudoInput.read(buffer, 0, 1024)) != -1) {
                if(bytes == 0) {
                    continue;
                }
                String data = String.valueOf(buffer, 0, bytes);
                updateGUI.updateLog(data);
                if (data.contains("[sudo] password")) {
                    //use the line below to hard code your sudo password so that you are not prompted for it
                    //char password[] = new char[]{'p','a','s','s','w','o','r','d'};
                    //or
                    //use the line below to be prompted to supply your sudo password
                    char password[] = JOptionPane.showInputDialog("sudo password:").toCharArray();
                    sudoOutput.write(password);
                    sudoOutput.write('\n');
                    sudoOutput.flush();
                    Arrays.fill(password, '\0');
                    pwTries = pwTries + 1;
                }
            }                    
        }
        catch (Exception ex) {
            updateGUI.updateLog("Error: " + ex.getMessage());
        } 
    }

}//end class

