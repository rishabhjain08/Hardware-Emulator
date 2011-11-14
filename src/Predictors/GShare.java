package Predictors;

import PipelineSimulation.SimUtil;
import DataStructures.SaturationCounter;

public class GShare {
        private SaturationCounter[] satcounter;
        private String bhr;
        private int bhrlen;
        private int pcbitlen;
        
        public GShare(){
                this.bhrlen=6;
                this.pcbitlen=12;
                satcounter=new SaturationCounter[(int)Math.pow(2, this.pcbitlen)];
                for(int i=0;i<satcounter.length;i++){
                        satcounter[i]=new SaturationCounter();
                }
                this.bhr="";
                for(int i=0;i<this.bhrlen;i++)
                    this.bhr+="0";
        }

        public GShare(int bhrlen, int pcbitlen){
            this.bhrlen=bhrlen;
            this.pcbitlen=pcbitlen;
            satcounter=new SaturationCounter[(int)Math.pow(2, this.pcbitlen)];
            for(int i=0;i<satcounter.length;i++){
                    satcounter[i]=new SaturationCounter();
            }
            this.bhr="";
            for(int i=0;i<this.bhrlen;i++)
                this.bhr+="0";
        }

        public boolean predict(String pc){
            String subpc=pc.substring(0,this.pcbitlen);
            String XOR="";
            for(int i=0;i<Math.min(subpc.length(), this.bhr.length());i++){
                XOR+=String.valueOf((Integer.parseInt(Character.toString(bhr.charAt(i)))+Integer.parseInt(Character.toString(subpc.charAt(i))) )%2);
            }
            if(subpc.length()<this.bhrlen){
                XOR+=this.bhr.substring(subpc.length());
            }
            else
                XOR+=subpc.substring(this.bhrlen);
            int index=(int) SimUtil.converttoInteger(XOR);
            int myprediction=satcounter[index].getValue();
            return myprediction>1;
        }

        public void train(String pc,boolean taken){
            String subpc=pc.substring(0,this.pcbitlen);
            String XOR="";
            for(int i=0;i<Math.min(subpc.length(), this.bhr.length());i++){
                XOR+=String.valueOf((Integer.parseInt(Character.toString(bhr.charAt(i)))+Integer.parseInt(Character.toString(subpc.charAt(i))) )%2);
            }
            if(subpc.length()<this.bhrlen){
                XOR+=this.bhr.substring(subpc.length());
            }
            else
                XOR+=subpc.substring(this.bhrlen);
            int index=(int) SimUtil.converttoInteger(XOR);
            int dir=taken?1:-1;
            satcounter[index].update(dir);
            //update bhr
            if(taken)
                    bhr="1"+bhr;
            else
                    bhr="0"+bhr;
            bhr=bhr.substring(0, bhr.length()-1);
            //
        }
}