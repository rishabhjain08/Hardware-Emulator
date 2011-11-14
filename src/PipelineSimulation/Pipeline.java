package PipelineSimulation;

import GUI.GUIFrame;
import Memory.MemoryAccess;
import PipelineSimulation.InstructionFile.Instructionblock;
import PipelineSimulation.TraceFile.Traceblock;
import Predictors.TournamentPredictor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Pipeline {
    
    private List <Traceblock>tracerecords=new LinkedList();
    private List <Instructionblock>instrecords=new LinkedList();    
    int clockcycles=0;
    private int currinst=0;
    private MemoryAccess memaccesses;
    private TournamentPredictor tournament;
    private Instructionblock previnst=null;
    private Object[] stages;
    private int nstages=5;
    private List <Object[]>branches;
    
    public Pipeline(){
        memaccesses=new MemoryAccess();
        tournament=new TournamentPredictor();
        branches=new LinkedList();
    }
    
    public Pipeline(MemoryAccess mem, TournamentPredictor predictor){
        memaccesses=mem;
        tournament=predictor;
        branches=new LinkedList();
    }

    public void readFromFile(String tracefilename, String instfilename) throws FileNotFoundException{
        TraceFile trace=new TraceFile(new File(tracefilename));
        tracerecords = trace.getRecords();
        InstructionFile instruct=new InstructionFile(new File(instfilename));
        instrecords = instruct.getRecords();
        currinst=0;
    }
    
    private void push(Instructionblock instruction,Traceblock trace){
        //System.out.println("pushing "+instruction.getType());
        Iterator<Object[]> iterator = this.branches.iterator();
        while(iterator.hasNext()){
            Object[] next = iterator.next();
            next[2]=(Integer)next[2]+1;
        }
        for(int k=0;k<this.branches.size();k++){
            Object[] ob=this.branches.get(k);
            if((Integer)(ob[2])>2){
                //System.out.println("Training here");
                this.tournament.train((String)ob[0],((Boolean)ob[1]));
                this.branches.remove(k);
                k--;
            }
        }
        if(this.previnst!=null&&this.previnst.getType()==0){//bubble insertion
            int currtype=instruction.getType();
            if(currtype==1&&previnst.getRd()==instruction.getRs())//load then store
                this.clockcycles++;
            if(currtype==2&&(previnst.getRd()==instruction.getRt()||previnst.getRd()==instruction.getRs()))//load then alu
                this.clockcycles++;
            if(currtype==3&&previnst.getRd()==instruction.getRs())
                this.clockcycles++;
        }
        this.previnst=instruction;
        this.clockcycles++;//since output is coming every cycle
        boolean prediction=true;
        if(instruction.getType()==3)//predicing the outcome of branch
            prediction=tournament.predict(instruction.getPc());
        if(instruction.getType()==3&&prediction!=trace.getTaken()){//if the prediction was wrong flust the system
            for(int k=0;k<this.branches.size();k++){
                    Object[] ob=this.branches.get(k);
                    this.branches.remove(k);
                    this.tournament.train((String)ob[0],(Boolean)ob[1]);
            }
            tournament.train(instruction.getPc(), trace.getTaken());
            this.clockcycles+=2;
        }
        else if(instruction.getType()==3){
            branches.add(new Object[]{instruction.getPc(),trace.getTaken(),0});
        }
        if(instruction.getType()==0)//calculating the latency of mem stage
            this.clockcycles+=memaccesses.load(trace.getMem_addr())-1;
        else if(instruction.getType()==1)
            this.clockcycles+=memaccesses.store(trace.getMem_addr())-1;

//        for(int i=this.nstages-2;i>=0;i--){
//            this.stages[i+1]=this.stages[i];
//        }
//        this.stages[0]=new Object[]{instruction,trace};
//        int cycle_1=0,cycle_2=0,cycle_3=0,cycle_4=0,cycle_5=0;
//        
//        //isntruction fetch
//        if(this.stages[0]!=null){
//            cycle_1=1;
//        }
//        //instruction decode
//        if(this.stages[1]!=null){
//            cycle_2=1;
//        }
//        //execution
//        
//        //mem stage
//        
//        //write back
    }
    
    public boolean nextInstruction(){
        if(currinst>=this.tracerecords.size())
            return false;
        Instructionblock block=null;
        for(int k=0;k<this.instrecords.size();k++){
            if(this.tracerecords.get(currinst).getPc().equals(this.instrecords.get(k).getPc())){
                block=this.instrecords.get(k);
                break;
            }
        }
        this.push(block,this.tracerecords.get(currinst));
        currinst++;
        return true;
    }
    
    public int getInstructionsExecuted(){
        return this.currinst;
    }
    
    public int getCurrentClockCycles(){
        return this.clockcycles;
    }

    public TournamentPredictor getPredictor(){
        return this.tournament;
    }
    
    public MemoryAccess getMemory(){
        return this.memaccesses;
    }

    public static void main(String[] argv) throws IOException{
        Pipeline piped=new Pipeline();
        
        if(argv.length>1){
              //For input from terminal
              piped.readFromFile(argv[0], argv[1]);
        }
        else if(argv.length==0){
            GUIFrame gui=new GUIFrame();
            gui.startGUI(null);
            return;
        }

          else{
              System.out.println("Not A Valid Input");
              return;
          }
    
        /*
        
         * //For io input
        
         * BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
         * String filenames = reader.readLine();
        
         * piped.readFromFile(filenames.substring(0, filenames.indexOf(' ')),filenames.substring(filenames.indexOf(' ')+1));
        
         */
         
        
         
        
//          //defalut
//     
//          piped.readFromFile("dynamicTrace.txt", "staticTrace.txt");
        
         
        
    while(piped.nextInstruction())
        piped.nextInstruction();
        double IPC=-1.0;
        if(piped.getCurrentClockCycles()!=0)
            IPC=((double)piped.getInstructionsExecuted()/piped.getCurrentClockCycles());
        int ipc=(int) (IPC*100);
        IPC=(double)ipc/100;
        double l1_miss_rate=-1.0;
        if((piped.getMemory().getL1Cache().getMisses()+piped.getMemory().getL1Cache().getHits())!=0)
            l1_miss_rate=((double)piped.getMemory().getL1Cache().getMisses())/(piped.getMemory().getL1Cache().getMisses()+piped.getMemory().getL1Cache().getHits());
        double l2_miss_rate=-1.0;
        if((piped.getMemory().getL2Cache().getMisses()+piped.getMemory().getL2Cache().getHits())!=0)
            l2_miss_rate=((double)piped.getMemory().getL2Cache().getMisses())/(piped.getMemory().getL2Cache().getMisses()+piped.getMemory().getL2Cache().getHits());
        int branch_pred_acc=piped.getPredictor().getBranch_predictor_accesses();
        int branc_pred_misses=piped.getPredictor().getBranch_predictor_misses();
        double branch_prediction_accuracy=-1.0;
        if(branch_pred_acc!=0)
            branch_prediction_accuracy=100*((double)(branch_pred_acc-branc_pred_misses))/branch_pred_acc;
        String sIPC=String.valueOf(IPC);
        if(sIPC.indexOf('.')!=-1)
            sIPC=sIPC.substring(0, Math.min(sIPC.length(), sIPC.indexOf('.')+3));
        String sl1_miss_rate=String.valueOf(l1_miss_rate);
        if(sl1_miss_rate.indexOf('.')!=-1)
            sl1_miss_rate=sl1_miss_rate.substring(0, Math.min(sl1_miss_rate.length(), sl1_miss_rate.indexOf('.')+3));
        String sl2_miss_rate=String.valueOf(l2_miss_rate);
        if(sl2_miss_rate.indexOf('.')!=-1){
            sl2_miss_rate=sl2_miss_rate.substring(0, Math.min(sl2_miss_rate.length(), sl2_miss_rate.indexOf('.')+3));
        }
        String sbranch_prediction_accuracy=String.valueOf(branch_prediction_accuracy);
        if(sbranch_prediction_accuracy.indexOf('.')!=-1)
            sbranch_prediction_accuracy=sbranch_prediction_accuracy.substring(0, Math.min(sbranch_prediction_accuracy.length(),sbranch_prediction_accuracy.indexOf('.')+3));
        System.out.println("ipc : "+sIPC+"\nl1_miss_rate : "+sl1_miss_rate+"%\nl2_miss_rate : "+sl2_miss_rate+"%\nbranch_prediction_accuracy : "+sbranch_prediction_accuracy+"%\nclock cycles : "+piped.getCurrentClockCycles()+"\nl1_misses : "+piped.getMemory().getL1Cache().getMisses()+"\nl2misses : "+piped.getMemory().getL2Cache().getMisses()+"\nbranch accesses : "+piped.getPredictor().getBranch_predictor_accesses()+"\nbranch misses : "+piped.getPredictor().getBranch_predictor_misses());
}
}
