package controlator;

import java.util.HashMap;
import modelo.Parser;

public class StringSplitterToDoEverithing {

    private Parser p = new Parser();

    public int setInstructionSetByString(String instructionsOnString) {
        HashMap<String, String> instructions = new HashMap<>();
        String[] instructionsOnStringSplitted = instructionsOnString.split("\n");
        for (int i = 0; i < instructionsOnStringSplitted.length; i++) {
            String[] individualInstructionSplitted = instructionsOnStringSplitted[i].split(" ");
            if(individualInstructionSplitted.length != 2){
                return (i+1);
            }
            instructions.put(individualInstructionSplitted[0], individualInstructionSplitted[1]);
        }
        p.setInstructionSet(instructions);
        return -1;
    }

    public void setConstantSetByString(String constantSet) {
        HashMap<String, String> constants = new HashMap<>();
        String[] constantsOnStringSplitted = constantSet.split("\n");
        for (int i = 0; i < constantsOnStringSplitted.length; i++) {
            String[] individualConstantSplitted = constantsOnStringSplitted[i].split(" ");
            constants.put(individualConstantSplitted[0], individualConstantSplitted[1]);
        }
        p.setConstantSet(constants);
    }

    public StringSplitterToDoEverithing(String instructionSetOnString) {
        setInstructionSetByString(instructionSetOnString);
    }

    public StringSplitterToDoEverithing() {

    }

    public String[] toMachineCode(String instructions, String constantSet, int instLenght, int memoryRange) {
        if (!constatsLineCorrectAnalizer(constantSet)) {
            String[] arr = {"Error en las constantes.", "Error en las constantes."};
            return arr;
        }
        setConstantSetByString(constantSet);
        String[] instructionsOnLine = instructions.split("\n");
        String[][] instruccionesEnTres = new String[instructionsOnLine.length][3];
        for (int i = 0; i < instructionsOnLine.length; i++) {
            String[] instructionActual = instructionsOnLine[i].split(" ");
            if (!instructionLineCorrectAnalizer(instructionsOnLine[i], p)) {
                String[] arr = {"Error en linea " + (i + 1), "Error en linea " + (i + 1)};
                return arr;
            }
            if (instructionActual.length == 1) {
                instruccionesEnTres[i][0] = "";
                instruccionesEnTres[i][1] = instructionActual[0];
                instruccionesEnTres[i][2] = "";
            } else if (instructionActual.length == 2) {
                if (p.isXAnInstruction(instructionActual[0])) {
                    instruccionesEnTres[i][0] = "";
                    instruccionesEnTres[i][1] = instructionActual[0];
                    instruccionesEnTres[i][2] = instructionActual[1];
                } else {
                    instruccionesEnTres[i][0] = instructionActual[0];
                    instruccionesEnTres[i][1] = instructionActual[1];
                    instruccionesEnTres[i][2] = "";
                }
            } else {
                instruccionesEnTres[i] = instructionActual;
            }
        }
        if (instLenght == 1) {
            String[] code = p.assembler1byteInstruction(instruccionesEnTres);
            StringBuilder machineCodeA = new StringBuilder();
            StringBuilder machineCodeB = new StringBuilder();
            for (int i = 0; i < code.length; i++) {
                machineCodeA.append(code[i]);
                machineCodeA.append(" ");
                if ((i + 1) % 11 == 0) {
                    machineCodeA.append("\n");
                }

                machineCodeB.append("X\"");
                machineCodeB.append(code[i]);
                machineCodeB.append("\", ");
                if ((i + 1) % 8 == 0) {
                    machineCodeB.append("\n");
                }
            }
            for (int i = code.length; i < memoryRange; i++) {
                machineCodeB.append("X\"");
                machineCodeB.append("00");
                machineCodeB.append("\", ");
                if ((i + 1) % 8 == 0) {
                    machineCodeB.append("\n");
                }
            }
            if (memoryRange < code.length) {
                machineCodeB = new StringBuilder("Las instrucciones no caben en la memoria.");
            }
            String[] arr = {machineCodeA.toString(), machineCodeB.toString()};
            return arr;
        } else {
            String[] code = p.assembler2byteInstructions(instruccionesEnTres);
            StringBuilder machineCodeA = new StringBuilder();
            StringBuilder machineCodeB = new StringBuilder();
            for (int i = 0; i < code.length; i++) {
                machineCodeA.append(code[i]);
                machineCodeA.append(" ");
                if ((i + 1) % 11 == 0) {
                    machineCodeA.append("\n");
                }

                machineCodeB.append("X\"");
                machineCodeB.append(code[i]);
                machineCodeB.append("\", ");
                if ((i + 1) % 8 == 0) {
                    machineCodeB.append("\n");
                }
            }
            for (int i = code.length; i < memoryRange; i++) {
                machineCodeB.append("X\"");
                machineCodeB.append("00");
                machineCodeB.append("\", ");
                if ((i + 1) % 8 == 0) {
                    machineCodeB.append("\n");
                }
            }
            if (memoryRange < code.length) {
                machineCodeB = new StringBuilder("Las instrucciones no caben en la memoria.");
            }
            String[] arr = {machineCodeA.toString(), machineCodeB.toString()};
            return arr;
        }
    }

    public boolean instructionLineCorrectAnalizer(String line, Parser p) {
        String[] lineSplitted = line.split(" ");
        if (lineSplitted.length == 1) {
            if (p.isXAnInstruction(lineSplitted[0])) {
                return true;
            }
        } else if (lineSplitted.length == 2) {
            if (p.isXAnInstruction(lineSplitted[0]) || p.isXAnInstruction(lineSplitted[1])) {
                if (p.isXAnInstruction(lineSplitted[0]) && p.isXAnInstruction(lineSplitted[1])) {
                    return false;
                } else {
                    return true;
                }
            }
        } else if (lineSplitted.length == 3) {
            if (p.isXAnInstruction(lineSplitted[0])) {
                return false;
            } else if (p.isXAnInstruction(lineSplitted[2])) {
                return false;
            } else {
                if (p.isXAnInstruction(lineSplitted[1])) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean constatsLineCorrectAnalizer(String constantsOnString) {
        String[] constantsSplitted = constantsOnString.split("\n");
        for (int i = 0; i < constantsSplitted.length; i++) {
            String[] oneLineConStrings = constantsSplitted[i].split(" ");
            if (oneLineConStrings.length != 2) {
                return false;
            }
            if (oneLineConStrings[1].length() != 2) {
                return false;
            }
        }
        return true;
    }
    
    public boolean instructionSetLineCorrectAnalizer(String instructionsOnString) {
        String[] instructionssSplitted = instructionsOnString.split("\n");
        for (int i = 0; i < instructionssSplitted.length; i++) {
            String[] oneLineConStrings = instructionssSplitted[i].split(" ");
            if (oneLineConStrings.length != 2) {
                return false;
            }
            if (oneLineConStrings[1].length() != 2) {
                return false;
            }
        }
        return true;
    }

    public String getInstructionsOnString(){
        return p.getInstructionSetActualOnString();
    }
}
