package Predictors;

import PipelineSimulation.SimUtil;
import DataStructures.SaturationCounter;


public class Bimodal {
	private SaturationCounter[] satcounter;
        private int PCBITLEN;
        
	public Bimodal(){
            this.PCBITLEN=10;
		satcounter=new SaturationCounter[(int)Math.pow(2, PCBITLEN)];
		for(int i=0;i<satcounter.length;i++){
			satcounter[i]=new SaturationCounter();
		}
	}

        public Bimodal(int pcbitlen){
            satcounter=new SaturationCounter[(int)Math.pow(2, pcbitlen)];
            for(int i=0;i<satcounter.length;i++){
                    satcounter[i]=new SaturationCounter();
            }
        }

        public boolean predict(String pc){
            int index=(int) SimUtil.converttoInteger(pc.substring(0,this.PCBITLEN));
            int myprediction=satcounter[index].getValue();
            return myprediction>1;
        }
        
        public void train(String pc,boolean taken){
            int index=(int) SimUtil.converttoInteger(pc.substring(0,this.PCBITLEN));
            int dir=taken?1:-1;
            satcounter[index].update(dir);
        }
}
