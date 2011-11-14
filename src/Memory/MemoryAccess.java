package Memory;

public class MemoryAccess {

    private int L1_ACCESS_TIME;
    private int L2_ACCESS_TIME;
    private int MEM_ACCESS_TIME;
    private int L1_HIT;
    private int L2_HIT;
    private int L2_MISS;
    private L1Cache l1;
    private L2Cache l2;
    public static int PRESENT=1,ABSENT=-1,DONT_KNOW=0;
    public static int LRU=-1,FIFO=1;
    
    public MemoryAccess() {
        this.L1_ACCESS_TIME=1;
        this.L2_ACCESS_TIME=8;
        this.MEM_ACCESS_TIME=200;
        this.L1_HIT=this.L1_ACCESS_TIME;
        this.L2_HIT=this.L1_ACCESS_TIME+this.L2_ACCESS_TIME;
        this.L2_MISS=this.L1_ACCESS_TIME+this.L2_ACCESS_TIME+this.MEM_ACCESS_TIME;
        l1=new L1Cache();
        l2=new L2Cache();
    }

    public MemoryAccess(L1Cache l1cache, L2Cache l2cache, int MEM_ACCESS_TIME) {
        this.L1_ACCESS_TIME=l1cache.ACCESS_CYCLE;
        this.L2_ACCESS_TIME=l2cache.ACCESS_CYCLE;
        this.MEM_ACCESS_TIME=MEM_ACCESS_TIME;
        this.L1_HIT=this.L1_ACCESS_TIME;
        this.L2_HIT=this.L1_ACCESS_TIME+this.L2_ACCESS_TIME;
        this.L2_MISS=this.L1_ACCESS_TIME+this.L2_ACCESS_TIME+this.MEM_ACCESS_TIME;
        l1=l1cache;
        l2=l2cache;
    }
    
    public int load(String mem_addr){
        if(l1.access(mem_addr))
            return L1_HIT;
        else if(l2.access(mem_addr)){
            return L2_HIT;
        }
        else{
            int clock_l2=l2.store(mem_addr,ABSENT);
            int clock_l1=l1.store(mem_addr,l2,ABSENT);
            return L2_MISS+clock_l2+clock_l1;
        }
    }
    
    public int store(String mem_addr){
        return this.l1.store(mem_addr, this.l2,DONT_KNOW);
    }
    
    public L1Cache getL1Cache(){
        return this.l1;
    }

    public L2Cache getL2Cache(){
        return this.l2;
    }
}