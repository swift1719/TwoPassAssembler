/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twopassassmbler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
/**
 *
 * @author Ayush
 */
public class PassTwo 
 {
	ArrayList<TableRow> SYMTAB,LITTAB;

	public PassTwo()
	{
		SYMTAB=new ArrayList<>();
		LITTAB=new ArrayList<>();
	}
	
	public void readtables()
	{
		BufferedReader br;
		String line;
		try
		{
			br=new BufferedReader(new FileReader("F:\\TwoPassAssmbler\\symbolTable.txt"));
			while((line=br.readLine())!=null)
			{
                                  
				String parts[]=line.split("\\s+");
				SYMTAB.add(new TableRow(parts[1], Integer.parseInt(parts[2]),Integer.parseInt(parts[0]) ));
			}
			br.close();
			br=new BufferedReader(new FileReader("F:\\TwoPassAssmbler\\litTable.txt"));
			while((line=br.readLine())!=null)
			{
                                  
				String parts[]=line.split("\\s+");
				LITTAB.add(new TableRow(parts[1], Integer.parseInt(parts[2]),Integer.parseInt(parts[0])));
			}
			br.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void generateCode(String filename) throws Exception
	{
		readtables();
		BufferedReader br=new BufferedReader(new FileReader(filename));

		BufferedWriter bw=new BufferedWriter(new FileWriter("F:\\TwoPassAssmbler\\outputPassTwo.txt"));
		String line,code;
		while((line=br.readLine())!=null)
		{
//                          line=line.replaceAll("[\\\\[\\\\]()]", "");
			String parts[]=line.split("\\s+");
			if(parts[0].contains("AD")||parts[0].contains("DL,02"))
			{
				bw.write("\n");
				continue;
			}
			else if(parts.length==2)
			{
				if(parts[0].contains("DL")) //DC INSTR
				{
					parts[0]=parts[0].replaceAll("[^0-9]", "");
					if(Integer.parseInt(parts[0])==1)
					{
						int constant=Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
						code="00\t00\t"+String.format("%02d", constant)+"\n";
						bw.write(code);						
					}
				}
                                  else if(parts[0].contains("IS"))
				{
					int opcode=Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
					if(opcode==10 || opcode==9)
					{
						if(parts[1].contains("S"))
						{
							int symIndex=Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
							code=String.format("%02d", opcode)+"\t00\t"+String.format("%02d", SYMTAB.get(symIndex).getAddress())+"\n";
							bw.write(code);
						}
						else if(parts[1].contains("L"))
						{
							int symIndex=Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
							code=String.format("%02d", opcode)+"\t00\t"+String.format("%02d", LITTAB.get(symIndex).getAddress())+"\n";
							bw.write(code);
						}
						
					}
				}
			}
			else if(parts.length==1 && parts[0].contains("IS"))
			{
				int opcode=Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
				code=String.format("%02d", opcode)+"\t00\t"+String.format("%02d", 0)+"\n";
				bw.write(code);
                                break;
			}
			else if(parts[0].contains("IS") && parts.length==3) //All OTHER IS INSTR
			{
                            int opcode=Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));

                            int regcode=Integer.parseInt(parts[1].replaceAll("[^0-9]",""));
			
                            if(parts[2].contains("S"))
                            {
                                    int symIndex=Integer.parseInt(parts[2].replaceAll("[^0-9]", ""));
                                    code=String.format("%02d", opcode)+"\t0"+regcode+"\t"+String.format("%03d", SYMTAB.get(symIndex).getAddress())+"\n";
                                    bw.write(code);
                            }
                            else if(parts[2].contains("L"))
                            {
                                    int symIndex=Integer.parseInt(parts[2].replaceAll("[^0-9]", ""));
                                    code=String.format("%02d", opcode)+"\t0"+regcode+"\t"+String.format("%03d", LITTAB.get(symIndex).getAddress())+"\n";
                                    bw.write(code);
                            }		
			}	
		}
		bw.close();
		br.close();
	}
}