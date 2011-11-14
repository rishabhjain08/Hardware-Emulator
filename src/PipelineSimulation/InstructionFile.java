package PipelineSimulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class InstructionFile {

    private List <Instructionblock>list = new LinkedList();
    InstructionFile(File f) throws FileNotFoundException {
      
        Scanner scan=new Scanner(f);
        while(scan.hasNextLine()){
            Instructionblock block=new Instructionblock();
            String line=scan.nextLine();
            int space1=line.indexOf(' ');
            int space2=line.indexOf(' ', space1+1);
            int space3=line.indexOf(' ', space2+1);
            int space4=line.indexOf(' ', space3+1);
            block.setPc(SimUtil.coverttoBitString(line.substring(0, space1)));
            block.setType(Integer.parseInt(line.substring(space1+1, space2)));
            block.setRs(Integer.parseInt(line.substring(space2+1, space3)));
            block.setRt(Integer.parseInt(line.substring(space3+1, space4)));
            block.setRd(Integer.parseInt(line.substring(space4+1)));
            list.add(block);
        }
    }
    
    List getRecords(){
        return list;
    }
    
    class Instructionblock{
        String pc;
        int type,rs,rt,rd;

        Instructionblock(){
            pc="";
            type=rs=rt=rd=0;
        }
        public String getPc() {
            return pc;
        }

        public void setPc(String pc) {
            this.pc = pc;
        }

        public int getRd() {
            return rd;
        }

        public void setRd(int rd) {
            this.rd = rd;
        }

        public int getRs() {
            return rs;
        }

        public void setRs(int rs) {
            this.rs = rs;
        }

        public int getRt() {
            return rt;
        }

        public void setRt(int rt) {
            this.rt = rt;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    
}
