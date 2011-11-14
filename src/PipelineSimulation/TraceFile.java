package PipelineSimulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class TraceFile {

    private List <Traceblock>list = new LinkedList();
    TraceFile(File f) throws FileNotFoundException {
      
        Scanner scan=new Scanner(f);
        while(scan.hasNextLine()){
            Traceblock block=new Traceblock();
            String line=scan.nextLine();
            int space1=line.indexOf(' ');
            int space2=line.indexOf(' ', space1+1);
            block.setPc(SimUtil.coverttoBitString(line.substring(0, space1)));
            block.setMem_addr(SimUtil.coverttoBitString(line.substring(space1+1,space2)));
            block.setTaken(Integer.parseInt(line.substring(space2+1))==0?false:true);
            list.add(block);
        }
    }
    
    List getRecords(){
        return list;
    }
        
    class Traceblock{
        private String pc,mem_addr;
        private boolean taken;
        Traceblock(){
            pc="";
            mem_addr="";
            taken=true;
        }

        public String getMem_addr() {
            return mem_addr;
        }

        public void setMem_addr(String mem_addr) {
            this.mem_addr = mem_addr;
        }

        public String getPc() {
            return pc;
        }

        public void setPc(String pc) {
            this.pc = pc;
        }

        public boolean getTaken() {
            return taken;
        }

        public void setTaken(boolean taken) {
            this.taken = taken;
        }
        
    }

    
}
