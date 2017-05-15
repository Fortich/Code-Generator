package modelo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class Parser {

    private HashMap<String, String> instructionSet;
    private HashMap<String, String> constantSet;
    
    public Parser() { 
        //Constructor que crea un parser con datos por default
        HashMap<String, String> instructionSetP = new HashMap<>();
        //Con un instructionSet inicializado
        instructionSetP.put("loadA", "00");
        instructionSetP.put("storeA", "20");
        instructionSetP.put("addA", "40");
        instructionSetP.put("subA", "60");
        instructionSetP.put("inA", "80");
        instructionSetP.put("outA", "A0");
        instructionSetP.put("JZ", "C0");
        instructionSetP.put("JPOS", "E0");
        //Sin constantes
        this.instructionSet = instructionSetP;
        this.constantSet = new HashMap<>();
    }
    
    public Parser(HashMap instrungtionSet, HashMap constantSet){
        //Constructor que crea un parser, dado un instructionSet y una lista de constantes
        this.constantSet = constantSet;
        this.instructionSet = instrungtionSet;
    }
    
    public void setConstantSet(HashMap constantSet){
        //Setter de constantes
        this.constantSet = constantSet;
    }
    
    public void setInstructionSet(HashMap instructionSet){
        //Setter de instrucciones
        this.instructionSet = instructionSet;
    }
    
    public HashMap getInstructionSet(){
        return instructionSet;
    }
    
    public HashMap getConstantSet(){
        return constantSet;
    }

    private String getOneByteInstruction(String instruction, String address) {
        //Esta función retorna el valor de una instrucción de tamaño 1 byte.
        //Para la realización de la función se necesita una instrucción y la dirección
        //que quiere manipular.
        
        //Primero obtenemos el valor entero de la instrucción, obteniendo su valor en
        //hexadecimal, guardado en el diccionario, y luego convirtiéndolo a integer
        int compiled = Integer.parseInt(instructionSet.get(instruction), 16);
        //Si la dirección es no es una cadena vacía, podemos sumar la dirección dada a
        //la instrucción requerida, en otro caso, la dirección no será sumada puesto
        //que la dirección no perjudicaría a la instrucción
        if (!address.equals("")) {
            compiled += Integer.parseInt(address, 16);
        }
        //La instrucción la pasamos de ser un integer a una cadena en representación
        //hexadecimal, y si ésta terminó siendo un número menor a 1 byte en tamaño,
        //agregamos un cero a la derecha para su correcta interpretación
        String res = Integer.toHexString(compiled);
        if (res.length() != 2) {
            res = "0" + res;
        }
        //Al final, retornamos la cadena y las letras se pasan a mayúsculas
        return res.toUpperCase();
    }

    private String[] getTwoByteInstruction(String instruction, String address) {
        //Esta función retorna el valor de una instrucción de tamaño 2 byte en un arreglo
        //de dos String que contienen los valores en hexadecimal que deben ser puestos en
        //la memoria para satisfacer la realización de la instrucción.
        
        //Para la realización de la función se necesita una instrucción y la dirección
        //que se quiere manipular
        
        //Para ello, primero verificamos si la dirección es vacía, si es así, la tomamos como
        //00, puesto que, seguramente se trata de una instrucción que no necesita dirección
        if (address.equals("")) {
            address = "00";
        }
        //Se construye un arreglo con el significado de la instrucción y la dirección modificada,
        //posteriormente se retorna.
        String[] arr = {instructionSet.get(instruction).toUpperCase(), address.toUpperCase()};
        return arr;
    }

    public String[] assembler1byteInstruction(String[][] instructions) {
        //Esta función retornará un arreglo de Strings con los valores que ha de tener
        //la memoria para poder realizar dado conjunto de instrucciones
        
        //Primero inicializamos una variable que almacenará el número de variables en 
        //nuestro conjunto de instrucciones
        int variables = 0;
        //Y creamos un diccionario de significados, que contendrán las etiquetas, labels, o
        //variables junto la dirección a la que debe apunta
        HashMap<String, String> meanings = new HashMap<>();
        for (int i = 0; i < instructions.length; i++) {
            //Recorremos todas las instrucciones verificando sus labels, si ésta no es vacía
            //quiere decir que hay que guardar su nombre y significado. Ésta verificación
            //es para no confundir etiquetas con variables a reservar
            
            //La dirección a la que apunta la etiqueta es la dirección de la línea de
            //instrucción, por ser instrucciones de 1 byte
            if(!instructions[i][0].equals("")){
                String dir = Integer.toHexString(i);
                if (dir.length() != 2) {
                    dir = "0" + dir;
                }
                meanings.put(instructions[i][0], dir);
            }
        }
        //Por defecto, decimos que la cadena vacía significa "00"
        meanings.put("", "00");
        //Creamos una lista de variables y constantes, que iremos llenando conforme vamos encontrando
        LinkedList<String> varsAndConstants = new LinkedList<>();
        for (int i = 0; i < instructions.length; i++) {
            //Recorremos de nuevo las instrucciones verificando si la dirección no es vacía
            if (!instructions[i][2].equals("")) {
                //Si no es vacía, verificamos si la dirección ya tiene significado
                if (!meanings.containsKey(instructions[i][2])) {
                    if (constantSet.containsKey(instructions[i][2])) {
                        //Si no tiene significado, y es una constante, entonces agregamos
                        //la constante a la lista de variables y constantes con su valor
                        //inicial
                        varsAndConstants.add((String) (constantSet.get(instructions[i][2])));
                    } else {
                        //Si no tiene significado, y no es una constante, la agregamos a la lista
                        //de variables y constantes con un valor de 00
                        varsAndConstants.add("00");
                    }
                    //Creamos una nueva dirección, que al ser instrucciones de 1 byte, las colocamos
                    //inmediatamente después de las instrucciones
                    String dir = Integer.toHexString(instructions.length + variables);
                    if (dir.length() != 2) {
                        dir = "0" + dir;
                    }
                    //Agregamos a nuestro diccionario el significado o dirección de la variable o constante
                    meanings.put(instructions[i][2], dir);
                    //Y aumentamos el número de variables en uno
                    variables++;
                }
            }
        }
        //Finalmente, teniendo una lista de variables y un diccionario de direcciones, procedemos 
        //a ensamblar cada línea
        
        //Creamos un arreglo de cadenas que tendrá todas las instrucciones de máquina y además,
        //tendrá la inicialización de las variables al final
        String[] assembled = new String[instructions.length + variables];
        //Empezamos a recorrer nuevamente las instrucciones
        for (int i = 0; i < instructions.length; i++) {
            //Y a cada espacio en nuestro arreglo vamos agregando el significado individual, con
            //la instrucción y la dirección
            assembled[i] = getOneByteInstruction(instructions[i][1], meanings.get(instructions[i][2]));
        }
        //Empezamos a recorrer las variables
        for (int i = instructions.length; i < assembled.length; i++) {
            //Y a cada espacio, vamos agregando los valores iniciales de las variables
            assembled[i] = varsAndConstants.get(i - instructions.length);
        }
        return assembled;
    }

    public String[] assembler2byteInstructions(String[][] instructions) {
        //Esta función retornará un arreglo de Strings con los valores que ha de tener
        //la memoria para poder realizar dado conjunto de instrucciones
        
        //Primero inicializamos una variable que almacenará el número de variables en 
        //nuestro conjunto de instrucciones
        int variables = 0;
        //Y creamos un diccionario de significados, que contendrán las etiquetas, labels, o
        //variables junto la dirección a la que debe apunta
        HashMap<String, String> meanings = new HashMap<>();
        for (int i = 0; i < instructions.length; i++) {
            //Recorremos todas las instrucciones verificando sus labels, si ésta no es vacía
            //quiere decir que hay que guardar su nombre y significado. Ésta verificación
            //es para no confundir etiquetas con variables a reservar
            
            //La dirección a la que apunta la etiqueta es la dirección de la línea de
            //instrucción multiplicado por dos, por ser instrucciones de 2 byte
            if(!instructions[i][0].equals("")){
                String dir = Integer.toHexString(i*2);
                if (dir.length() != 2) {
                    dir = "0" + dir;
                }
                meanings.put(instructions[i][0], dir);
            }
        }
        //Por defecto, la cadena vacía significa la dirección 00
        meanings.put("", "00");
        //Creamos una lista que lamacenará los valores iniciales de las variables y constantes
        LinkedList<String> varsAndConstants = new LinkedList<>();
        //Empezamos a recorrer de nuevo las instrucciones para ver los valores iniciales
        //de las variables
        for (int i = 0; i < instructions.length; i++) {
            //Verificamos si en la instrucción, el espacio de la variable no es una cadena vacía.
            if (!instructions[i][2].equals("")) {
                //Si de esta variable o constante no conocemos su significado, entonces
                //verificamos:
                if (!meanings.containsKey(instructions[i][2])) {
                    //Si es una constante, entonces guardamos la variable en la lista con
                    //su requerido valor inicial
                    if (constantSet.containsKey(instructions[i][2])) {
                        varsAndConstants.add((String) (constantSet.get(instructions[i][2])));
                    } else {
                    //Si no o es, guardamos con el valor inicial de 00
                        varsAndConstants.add("00");
                    }
                    //Y finalmente, en el diccionario, guardamos la variable con la dirección
                    //del número de instrucciones multiplicado por dos (por der instrucciones
                    //de tamaño 2 bytes), y el número de variable que lleva.
                    String dir = Integer.toHexString(2 * instructions.length + variables);
                    if (dir.length() != 2) {
                        dir = "0" + dir;
                    }
                    meanings.put(instructions[i][2], dir);
                    //Finalmente aumentamos nuestro contador
                    variables++;
                }
            }
        }
        //Inicializamos un arreglo de Strings que contendrá nuestras instrucciones de máquina en hexa
        //y al final, tendrá la inicialización de las variables
        String[] assembled = new String[2 * instructions.length + variables];
        for (int i = 0; i < instructions.length; i++) {
            //Obtenemos el valor de cada instrucción por semarado con la función
            //getTwoByteInstruction
            String[] tem = getTwoByteInstruction(instructions[i][1], meanings.get(instructions[i][2]));
            //Y a cada instrucción le asignamos su respectivo LOW y HIGH
            assembled[2 * i] = tem[1];
            assembled[2 * i + 1] = tem[0];
        }
        for (int i = 0; i < variables; i++) {
            //Finalmente, colocamos los valores iniciales de la memoria
            assembled[i + instructions.length * 2] = varsAndConstants.get(i);
        }
        return assembled;
    }
    
    public boolean isXAnInstruction(String x){
        //Esta función nos retornará si X es una instrucción dentro del instrucction set
        return instructionSet.containsKey(x);
    }
    
    public String getInstructionSetActualOnString(){
        
        Set<String> instructions = instructionSet.keySet();
        String[][] instructionsOnArray = new String[instructions.size()][2];
        int i = 0;
        for(String s: instructions){
            instructionsOnArray[i][0] = s;
            instructionsOnArray[i][1] = instructionSet.get(s);
            i++;
        }
        Arrays.sort(instructionsOnArray, 0, instructionsOnArray.length, new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1[1], o2[1]);
            }
        });
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < instructionsOnArray.length; j++) {
            sb.append(instructionsOnArray[j][0]);
            sb.append(" ");
            sb.append(instructionsOnArray[j][1]);
            sb.append("\n");
        }
        return sb.toString();
    }

}
