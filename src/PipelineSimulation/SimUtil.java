package PipelineSimulation;

public class SimUtil {
    public static String coverttoBitString(String hex){
        String without_x=hex.substring(hex.indexOf('x')+1).toUpperCase();
        String bitstring="";
        for(int i=0;i<without_x.length();i++){
            String charvalue="";
            switch(without_x.charAt(i)){
                case '0':
                    charvalue="0000";
                    break;
                case '1':
                    charvalue="0001";
                    break;
                case '2':
                    charvalue="0010";
                    break;
                case '3':
                    charvalue="0011";
                    break;
                case '4':
                    charvalue="0100";
                    break;
                case '5':
                    charvalue="0101";
                    break;
                case '6':
                    charvalue="0110";
                    break;
                case '7':
                    charvalue="0111";
                    break;
                case '8':
                    charvalue="1000";
                    break;
                case '9':
                    charvalue="1001";
                    break;
                case 'A':
                    charvalue="1010";
                    break;
                case 'B':
                    charvalue="1011";
                    break;
                case 'C':
                    charvalue="1100";
                    break;
                case 'D':
                    charvalue="1101";
                    break;
                case 'E':
                    charvalue="1110";
                    break;
                case 'F':
                    charvalue="1111";
                    break;
                default:
                    System.out.println("Not a vaild hex number");
                    return null;
            }
            bitstring+=charvalue;
        }
        String unsignedBitStringExtension = SimUtil.unsignedBitStringExtension(reverseString(bitstring), 32);
        return unsignedBitStringExtension;
    }
    
      public static long converttoInteger(String bitstring){
          long value=0;
            for(int i=0;i<bitstring.length();i++){
                char charac=bitstring.charAt(i);
                if(!Character.isDigit(charac)){
                    System.out.println("Not a valid bit string");
                    return -1;
                }
               if(Integer.parseInt(Character.toString(charac))==1)
                   value+=Math.pow(2, i); 
            }
            return value;
    }
    
      public static String reverseString(String s){
        String reversed="";
        int s_len=s.length();
        for(int i=0;i<s.length();i++){
            reversed+=s.charAt(s_len-i-1);
        }
        return reversed;
    }
      
      public static int getMinValIndex(int[] arr){
          int min=0;
          int i=0;
          for(i=0;i<arr.length;i++){
              if(arr[min]>arr[i])
                  min=i;
          }
          return min;
      }
      
      	public static int compareBitStrings(String s1, String s2){
		String zeros="";
		for(int i=0;i<Math.abs(s2.length()-s1.length());i++){
				zeros+="0";
		}
		if(s1.length()<s2.length()){
			s1+=zeros;
		}
		int len=s1.length();
		int s1num=0,s2num=0;
		for(int i=0;i<len;i++){
			s1num=Integer.parseInt(Character.toString(s1.charAt(len-i-1)));
			s2num=Integer.parseInt(Character.toString(s2.charAt(len-i-1)));
			if(s1num>s2num)
				return -1;
			else if(s2num>s1num)
				return 1;
		}
		return 0;
	}
        
        public static String converttoBitString(int integer){
            String bitstring="";
            while(integer!=0){
                bitstring+=String.valueOf(integer%2);
                integer=((int)integer/2);
            }
            return bitstring;
        }
        
        public static String unsignedBitStringExtension(String bitstring,int size){
            for(int i=0;i<size-bitstring.length();i++){
                bitstring+="0";
            }
            return bitstring;
        }

    
}
