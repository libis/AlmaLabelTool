package be.libis.utils;

public class Utils
{   
    public static String mod43(String str)  
    {  
        if (str == null || str.isEmpty())
        {
            return "Error";
        }
        
        str = str.toUpperCase();
        
        int sum=0;
        
        char[] charmod = new char[43];
        charmod[0]='0';
        charmod[1]='1';
        charmod[2]='2';
        charmod[3]='3';
        charmod[4]='4';
        charmod[5]='5';
        charmod[6]='6';
        charmod[7]='7';
        charmod[8]='8';
        charmod[9]='9';
        charmod[10]='A';
        charmod[11]='B';
        charmod[12]='C';
        charmod[13]='D';
        charmod[14]='E';
        charmod[15]='F';
        charmod[16]='G';
        charmod[17]='H';
        charmod[18]='I';
        charmod[19]='J';
        charmod[20]='K';
        charmod[21]='L';
        charmod[22]='M';
        charmod[23]='N';
        charmod[24]='O';
        charmod[25]='P';
        charmod[26]='Q';
        charmod[27]='R';
        charmod[28]='S';
        charmod[29]='T';
        charmod[30]='U';
        charmod[31]='V';
        charmod[32]='W';
        charmod[33]='X';
        charmod[34]='Y';
        charmod[35]='Z';
        charmod[36]='-';
        charmod[37]='.';
        charmod[38]=' ';
        charmod[39]='$';
        charmod[40]='/';
        charmod[41]='+';
        charmod[42]='%';
 
        int i=0;
        while (i<str.length())
        {
            int j=0;
            while (j<43)
            {
                if (str.charAt(i)==charmod[j])
                {
                    sum = sum + j;
                    j=42;
                }
                j++;
            }
            i++; 
        }
        try
        {
            return Character.toString(charmod[sum % 43]);
        }
        catch (Exception e)
        {
            return "Error";
        }
    }
}