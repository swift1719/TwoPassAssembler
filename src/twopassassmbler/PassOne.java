package twopassassmbler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.*;

/**
 *
 * @author Ayush
 */
public class PassOne {

    int lc = 0;
    int literalTableIndex = 0, poolTableIndex = 0;
    int symbolTableIndex = -1, litTabIndex = -1;
    HashMap<String, TableRow> symbolTable;
    ArrayList<TableRow> literalTable;
    ArrayList<Integer> poolTable;
    private BufferedReader br;
    BufferedWriter bw;
    MOTtable macOpcodeTab;
    
    public PassOne() {
        macOpcodeTab = new MOTtable();//initialising machine opcode table 
        symbolTable = new HashMap<>();
        literalTable = new ArrayList<>();
        poolTable = new ArrayList<>();
        lc = 0;
        poolTable.add(0);
    }

    public void parseFile() throws Exception {
        String prev= "";
        String ipFile="F:\\TwoPassAssmbler\\input.txt";
        String line, code;
        System.out.println("\t\t-------Intermediate Code-------");
        br = new BufferedReader(new FileReader(ipFile));//reading input file
        bw=new BufferedWriter(new FileWriter("F:\\TwoPassAssmbler\\outputOfOnePass.txt"));//reading output file
        while ((line = br.readLine()) != null) {
            
            String parts[] = line.split("\\s+");
            if (!parts[0].isEmpty()) //if label is present then adding to symbol table           
            {
                if (symbolTable.containsKey(parts[0])) {//updating symbol address
                    symbolTable.put(parts[0], new TableRow(parts[0], lc, symbolTable.get(parts[0]).getIndex()));
                } else {
                    symbolTable.put(parts[0], new TableRow(parts[0], lc, ++symbolTableIndex));
                }
            }
            // Processing OPCODES

            //handling assembler directives
            //ltorg
            if (parts[1].equals("LTORG")) {
                int ptr = poolTable.get(poolTableIndex);
                for (int j = ptr; j < literalTableIndex; j++) {
                    //adding new literals to literal table
                    literalTable.set(j, new TableRow(literalTable.get(j).getSymbol(), lc));
                    code = "(DL,01)\t(C," + literalTable.get(j).getSymbol() + ")";
                    System.out.println(code);
                    bw.write(code+"\n");
                    lc++;
                }
                //updating the pool table
                poolTableIndex++;
                poolTable.add(literalTableIndex);
            }
            //start
            if (parts[1].equals("START")) {
                lc = obtainValueOfString(parts[2]);
                code = "(AD,01)\t(C," + lc + ")";
                System.out.println(code);
                bw.write(code+"\n");
                prev = "START";
            }
            //origin
            if (parts[1].equals("ORIGIN")) {
                lc = obtainValueOfString(parts[2]);
                String splits[] = parts[2].split("\\+"); //Same for - SYMBOL //Add code
                code = "(AD,03)\t(S," + symbolTable.get(splits[0]).getIndex() + ")+" + Integer.parseInt(splits[1]);
                System.out.println(code);
//                bw.write(code+"\n");
            }

            //equ
            if (parts[1].equals("EQU")) {
                int loc = obtainValueOfString(parts[2]);
                
                if (parts[2].contains("+")) {
                    String splits[] = parts[2].split("\\+");
                    code = "(AD,04)\t(S," + symbolTable.get(splits[0]).getIndex() + ")+" + Integer.parseInt(splits[1]);
                } else if (parts[2].contains("-")) {
                    String splits[] = parts[2].split("\\-");
                    code = "(AD,04)\t(S," + symbolTable.get(splits[0]).getIndex() + ")-" + Integer.parseInt(splits[1]);
                } else {
                    try{
                        code = "(AD,04)\t(C," + Integer.parseInt(parts[2]) + ")";
                    }catch(Exception NumberFormatException){
                        code = "(AD,04)\t(C," + symbolTable.get(parts[2]).getAddress() + ")";
                    }
                }
                System.out.println(code);
                bw.write(code+"\n");
                if (symbolTable.containsKey(parts[0])) {//updaing label address if already present
                    symbolTable.put(parts[0], new TableRow(parts[0], loc, symbolTable.get(parts[0]).getIndex()));
                } else {
                    symbolTable.put(parts[0], new TableRow(parts[0], loc, ++symbolTableIndex));
                }
            }
            //for DL type instructions
            if (parts[1].equals("DC")) {//for declaring constant
                lc++;//length of instruction in one
                int constant = Integer.parseInt(parts[2].replace("'", ""));
                code = "(DL,02)\t(C," + constant + ")";
                System.out.println(code);
                bw.write(code+"\n");
            } else if (parts[1].equals("DS")) {//for declaring storage
                int size = Integer.parseInt(parts[2].replace("'", ""));
                code = "(DL,01)\t(C," + size + ")";
                System.out.println(code);
                bw.write(code+"\n");
                lc = lc + size;//length of instruction depends on size
                prev = "";
            }
            //for IS type instructions
            if (macOpcodeTab.getType(parts[1]).equals("IS")) {
                code = "(IS,0" + macOpcodeTab.getCode(parts[1]) + ")\t";//obtaining codes from MOT table
                int j = 2;
                String code2 = "";
                while (j < parts.length) {
                    parts[j] = parts[j].replace(",", "");
                    if (macOpcodeTab.getType(parts[j]).equals("CC")) {//for comparitive instructions
                        code2 += "(CC,0"+macOpcodeTab.getCode(parts[j]) + ")\t";
                    }
                    else if (macOpcodeTab.getType(parts[j]).equals("RG")) {//for registers
                        code2 += "(RG,0"+macOpcodeTab.getCode(parts[j]) + ")\t";
                    } 
                    else {
                        if (parts[j].contains("=")) {// for literals
                            parts[j] = parts[j].replace("=", "").replace("'", "");
                            literalTable.add(new TableRow(parts[j], -1, ++litTabIndex));//inserting literal in literal table
                            code2 += "(L," + (litTabIndex) + ")";
                            literalTableIndex++;
                        } else if (symbolTable.containsKey(parts[j])) {
                            int ind = symbolTable.get(parts[j]).getIndex();// if symbol already present in symbol table
                            code2 += "(S," + ind + ")";//then write corresponding symbol index in IC
                        } else {
                            symbolTable.put(parts[j], new TableRow(parts[j], -1, ++symbolTableIndex));//else insert symbol in symbol table
                            int ind = symbolTable.get(parts[j]).getIndex();
                            code2 += "(S," + ind + ")";
                        }
                    }
                    j++;
                }
                lc++;
                code = code + code2;
                System.out.println(code);
                bw.write(code+"\n");
            }
            //end
            if (parts[1].equals("END")) {
                int ptr = poolTable.get(poolTableIndex);
                code="";
                for (int j = ptr; j < literalTableIndex; j++) {
                    //inserting last pool of literals in the literal table
                    literalTable.set(j, new TableRow(literalTable.get(j).getSymbol(), lc));
                    code = "(DL,02)\t(C," + literalTable.get(j).symbol + ")";
                    lc++;
                }
                // poolTableIndex++;
                // poolTable.add(literalTableIndex);
                code += "\n(AD,02)";
                System.out.println(code);
                bw.write(code+"\n");
            }

        }
        bw.close();
        //Printing Symbol table
        printsymbolTable();
        //Printing Literal table
        PrintliteralTable();
        //Printing Pool table
        printpoolTable();
    }

    void PrintliteralTable() throws IOException {
        bw=new BufferedWriter(new FileWriter("F:\\TwoPassAssmbler\\litTable.txt"));//reading output file
        System.out.println();
        System.out.println("\t\t----LITERAL TABLE-----");
        //Processing literalTable
        System.out.println("Index\tLiteral\tAddress");
//        bw.write("Index\tLiteral\tAddress\n");
        for (int i = 0; i < literalTable.size(); i++) {
            TableRow row = literalTable.get(i);
            bw.write(i + "\t='" + row.getSymbol() + "'\t" + row.getAddress()+"\n");
            System.out.println(i + "\t='" + row.getSymbol() + "'\t" + row.getAddress());
        }
        bw.close();
    }

    void printpoolTable() throws IOException {
        bw=new BufferedWriter(new FileWriter("F:\\TwoPassAssmbler\\poolTable.txt"));//reading output file

        System.out.println();
        System.out.println("\t\t-----POOL TABLE-----");
        System.out.println("Index\tPool");
//        bw.write("Index\tPool\n");
        for (int i = 0; i < poolTable.size(); i++) {
            bw.write(i+"\t"+poolTable.get(i)+"\n");
            System.out.println(i + "\t" + poolTable.get(i));
        }
        bw.close();
    }

    void printsymbolTable() throws IOException {
        bw=new BufferedWriter(new FileWriter("F:\\TwoPassAssmbler\\symbolTable.txt"));//reading output file
        java.util.Set<String> keyset= symbolTable.keySet();
        System.out.println();
        String keyArr[]=new String[keyset.size()];
        keyset.toArray(keyArr);
        System.out.println("\t\t-----SYMBOL TABLE-----");
        System.out.println("Index\tSymbol\tAddress");
//        bw.write("Index\tSymbol\tAddress\n");
        String arr[]=new String[symbolTable.size()];
        
        for(int i=0;i<keyset.size();i++) {
            TableRow value = symbolTable.get(keyArr[i]);
            arr[value.getIndex()]=value.getSymbol();
        }
        for(int i=0;i<arr.length;i++){
            TableRow value= symbolTable.get(arr[i]);
            bw.write(value.getIndex() + "\t" + value.getSymbol() + "\t" + value.getAddress()+"\n");
            System.out.println(value.getIndex() + "\t" + value.getSymbol() + "\t" + value.getAddress());
        }
        bw.close();
    }

    //function to perform operations on string and get back resultant integer value
    public int obtainValueOfString(String str) {
        int temp = 0;
        if (str.contains("+")) {
            String splits[] = str.split("\\+");//splitting string to get addition of values
            temp = symbolTable.get(splits[0]).getAddress() + Integer.parseInt(splits[1]);
        } else if (str.contains("-")) {
            String splits[] = str.split("\\-");//splitting string to get subtraction of values
            temp = symbolTable.get(splits[0]).getAddress() - (Integer.parseInt(splits[1]));
        } else {
            if(symbolTable.containsKey(str)){
                 temp=symbolTable.get(str).getAddress();
            }else{
                 temp = Integer.parseInt(str);
            }
        }
        return temp;
    }
}

