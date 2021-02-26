// /*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
 package twopassassmbler;

// /**
// *
// * @author Ayush
// */
 public class TwoPassAssmbler {

//    /**
//     * @param args the command line arguments
//     */

    public static void main(String[] args) {
        PassOne one=new PassOne();
        try
        {
                one.parseFile();
                PassTwo pass2=new PassTwo();
                pass2.generateCode("F:\\TwoPassAssmbler\\outputOfOnePass.txt");
		
        }
        catch (Exception e) {
                System.out.println("Error: "+e);// TODO: handle exception
        }
    }
 }
