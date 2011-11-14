
package Memory;

import PipelineSimulation.SimUtil;

    public class L1Cache{
        
        private int bitindexlength;
        private String[] tags;
        private int associativity;
        private int curr_time; 
        private int time[];
        private int accesses,misses;
        public int ACCESS_CYCLE;
        private int tagsize;
        private int blocksizebits;
        private int replacement_policy;
        
        public int getAccesses() {
            return accesses;
        }

        public int getMisses() {
            return misses;
        }
        
        public int getHits(){
            return (this.accesses-this.misses);
        }
        
        public L1Cache(int blocksizebits, int bitindexlength, int associativity, int access_cycle, int replacement_policy){
            curr_time=0;
            accesses=0;
            misses=0;
            this.bitindexlength=bitindexlength;
            this.associativity=associativity;
            this.blocksizebits=blocksizebits;
            this.ACCESS_CYCLE=access_cycle;
            this.replacement_policy=replacement_policy;
            tagsize=32-this.bitindexlength-this.blocksizebits+this.associativity;
            tags=new String[(int)Math.pow(2, this.bitindexlength)];        
            time=new int[(int)Math.pow(2, this.bitindexlength)];
            for(int i=0;i<tags.length;i++)
                tags[i]=null;
        }
        
        public L1Cache(){
            curr_time=0;
            accesses=0;
            misses=0;
            this.bitindexlength=10;
            this.associativity=2;
            this.blocksizebits=5;
            this.ACCESS_CYCLE=1;
            this.replacement_policy=MemoryAccess.FIFO;
            tagsize=32-this.bitindexlength-this.blocksizebits+this.associativity;
            tags=new String[(int)Math.pow(2, this.bitindexlength)];        
            time=new int[(int)Math.pow(2, this.bitindexlength)];
            for(int i=0;i<tags.length;i++)
                tags[i]=null;
        }
        
        boolean access(String addr){
            accesses++;
            if(this.replacement_policy==MemoryAccess.LRU)
                curr_time++;
            String tag=addr.substring(32-this.tagsize);
            String indexpos=addr.substring(this.blocksizebits, this.blocksizebits+this.bitindexlength);
            String[] extra_bits=new String[this.associativity];
            int extra_bits_len=(int)(Math.round(Math.log(this.associativity)/Math.log(2)));
            for(int i=0;i<this.associativity;i++){
                extra_bits[i]=SimUtil.unsignedBitStringExtension(SimUtil.converttoBitString(i),extra_bits_len);
            }
            String[] pos=new String[this.associativity];
            for(int i=0;i<pos.length;i++)
                pos[i]=indexpos.substring(0, indexpos.length()-extra_bits_len)+extra_bits[i];
            for(int i=0;i<pos.length;i++)
                if(tags[(int)SimUtil.converttoInteger(pos[i])]!=null&&tags[(int)SimUtil.converttoInteger(pos[i])].equals(tag)){
                    if(this.replacement_policy==MemoryAccess.LRU)
                        time[(int)SimUtil.converttoInteger(pos[i])]=curr_time;
                    return true;
                }
            misses++;
            return false;
        }
        
        int store(String addr, L2Cache l2,int condition){
            curr_time++;
            int cycles=0;
            boolean access = true;
            if(condition==MemoryAccess.DONT_KNOW){
                this.access(addr);
                cycles+=this.ACCESS_CYCLE;//for finding a hit or miss
            }
            else if(condition==MemoryAccess.PRESENT){
                access=true;
            }
            else
                access=false;
            //cycles+=this.ACCESS_CYCLE;//for overwriting in L1 cache or adding new entry in L1cache
            if(access){
                return cycles;
            }
            String tag=addr.substring(32-this.tagsize);
            String indexpos=addr.substring(this.blocksizebits, this.blocksizebits+this.bitindexlength);
            String[] extra_bits=new String[this.associativity];
            int extra_bits_len=(int)(Math.round(Math.log(this.associativity)/Math.log(2)));
            for(int i=0;i<this.associativity;i++){
                extra_bits[i]=SimUtil.unsignedBitStringExtension(SimUtil.converttoBitString(i),extra_bits_len);
            }
            String[] pos=new String[this.associativity];
            int[] modtime=new int[this.associativity];
            for(int i=0;i<pos.length;i++){
                pos[i]=indexpos.substring(0, indexpos.length()-extra_bits_len)+extra_bits[i];
                modtime[i]=time[(int)SimUtil.converttoInteger(pos[i])];
            }
            int minValIndex = SimUtil.getMinValIndex(modtime);
            if(tags[(int)SimUtil.converttoInteger(pos[minValIndex])]!=null)
                cycles+=l2.store(SimUtil.unsignedBitStringExtension("", this.blocksizebits) +indexpos.substring(0, indexpos.length()-extra_bits_len)+tags[(int)SimUtil.converttoInteger(pos[minValIndex])],MemoryAccess.PRESENT);
            tags[(int)SimUtil.converttoInteger(pos[minValIndex])]=tag;
            time[(int)SimUtil.converttoInteger(pos[minValIndex])]=curr_time;
            return cycles;
        }
    }
