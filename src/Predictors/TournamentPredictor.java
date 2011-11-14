package Predictors;

import PipelineSimulation.SimUtil;

import DataStructures.SaturationCounter;


public class TournamentPredictor {
    
    private GShare gshare;
    private Bimodal bimodal;
    private Object[] select;
    private int branch_predictor_misses=0;
    private int branch_predictor_accesses=0;
    private int PCBITS;
    
    public TournamentPredictor (){
            this.gshare=new GShare();
            this.bimodal=new Bimodal();
            this.PCBITS=10;
            this.select = new Object[(int)Math.pow(2, this.PCBITS)];
    }

    public TournamentPredictor (GShare gshare,Bimodal bimodal,int pcbits){
            this.gshare=gshare;
            this.bimodal=bimodal;
            this.PCBITS=pcbits;
            this.select = new Object[(int)Math.pow(2, this.PCBITS)];
    }

    public int getBranch_predictor_accesses() {
        return branch_predictor_accesses;
    }

    public int getBranch_predictor_misses() {
        return branch_predictor_misses;
    }

    private int getPCIndex(String pc){
        pc=SimUtil.unsignedBitStringExtension(pc, 32);
        return (int) SimUtil.converttoInteger(pc.substring(0, PCBITS));
    }

    public boolean predict(String pc){
            pc=SimUtil.unsignedBitStringExtension(pc, 32);    
            this.branch_predictor_accesses++;
            int pcindex=0;
            pcindex=this.getPCIndex(pc);
            Object[] obj=(Object[]) this.select[pcindex];
            //System.out.println("modified Predicted pc : "+pc+" pc index : "+this.getPCIndex(pc));
            int predictor = 0;
            if(obj!=null)
                predictor=((SaturationCounter)obj[1]).getValue();
            else{
                //System.out.println("index is : "+this.getPCIndex(pc));
                SaturationCounter saturationCounter = new SaturationCounter();
                obj=new Object[]{pc,saturationCounter};
                predictor=((SaturationCounter)obj[1]).getValue();
                this.select[this.getPCIndex(pc)]=obj;
                //System.err.println("obj is : "+(Object[]) this.select[this.getPCIndex(pc)]);
            }
            
            boolean myprediction=false;
            boolean bimodalprediction=bimodal.predict(pc);
            boolean gshareprediction=gshare.predict(pc);
            if(predictor<2){
                    myprediction=bimodalprediction;
            }
            else{
                    myprediction=gshareprediction;
            }
            return myprediction;
    }
    
    public void train(String pc,boolean taken){
            pc=SimUtil.unsignedBitStringExtension(pc, 32);    
            this.branch_predictor_accesses++;
            Object[] obj=(Object[]) this.select[this.getPCIndex(pc)];
            //System.out.println("modified training pc : "+pc+" pc index : "+this.getPCIndex(pc));
            boolean bimodalprediction=bimodal.predict(pc);
            boolean gshareprediction=gshare.predict(pc);
            boolean myprediction=false;
            //
            int predictor=0;
            if(obj!=null)
                predictor=((SaturationCounter)obj[1]).getValue();
            else{
                //System.out.println("training for : "+pc);
                System.out.println("I should not be here\n");
                System.exit(0);
                SaturationCounter saturationCounter = new SaturationCounter();
                obj=new Object[]{pc,saturationCounter};
                predictor=((SaturationCounter)obj[1]).getValue();
            }
            if(predictor<2){
                    myprediction=bimodalprediction;
            }
            else{
                    myprediction=gshareprediction;
            }

            //
            //System.out.println("I am here\n");
            if(myprediction!=taken){
                //System.out.println("A branch miss\n");
                //System.exit(0);
                branch_predictor_misses++;
            }
            if(bimodalprediction==taken&&gshareprediction!=taken){
                    ((SaturationCounter)obj[1]).update(-1);
            }
            else if(bimodalprediction!=taken&&gshareprediction==taken){
                    ((SaturationCounter)obj[1]).update(1);
            }
            bimodal.train(pc,taken);
            gshare.train(pc,taken);
    }
}