import ORG.oclc.ber.BerString;
import ORG.oclc.ber.DataDir;
import ORG.oclc.LocalByteConverter.LocalByteConverter;
import ORG.oclc.LocalCharConverter.LocalCharConverter;
import ORG.oclc.util.Util;

public class DataDirTest {
    public DataDirTest() {
    }

    public static void main(String args[]) {
        BerString          rec;
        byte[]             usm94;
        DataDir            dir=new DataDir(0, 0);
        LocalByteConverter lbc=null;
        LocalCharConverter lcc=null;
        String             s;

        try {
            lbc=LocalByteConverter.getConverter("USM94");
            lcc=LocalCharConverter.getConverter("USM94");
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        dir.addUTF(1, 1, "dog");
        dir.addUTF(1, 1, "d|um|og");
        dir.addUTF(1, 1, "d|ce||ac|og");
        dir.addUTF(1, 1, "|$|dog");
        dir.addUTF(1, 1, "|dog");
        dir.addUTF(1, 1, "fran|ce|cais");
        dir.addUTF(1, 1, "|ga|");
        dir.addUTF(1, 1, "|ga|s");
        dir.addUTF(1, 1, "|ga|s|gb|");
        dir.addUTF(1, 1, "|ga||ay|");
        dir.addUTF(1, 1, "|ga||ay||gb|");
        dir.addUTF(1, 1, "CHARACTER SET TEST |ga||ay|RECORD: DIACRITICS.");
        dir.addUTF(1, 1, "Superscript zero problem Notes|p0|");
        System.out.println(dir);
        rec=new BerString(dir);
        dir=new DataDir(rec);
        for(dir=dir.child(); dir!=null; dir=dir.next()) {
            System.out.println("----");
            System.out.print("utf8: "+Util.byteArrayToString(dir.getBytes()));
            s=dir.getUTFString();
            try {
                System.out.print("Unicode:\n"+
                    Util.byteArrayToString(toBytes(s)));
                System.out.println("oclc ascii:"+Util.toBars(s));
                if(lcc!=null) {
                    usm94=lcc.convertAll(s.toCharArray());
                    System.out.println("usm94: "+
                        Util.byteArrayToString(usm94));
                    if(lbc!=null)
                        System.out.print("Unicode again:\n"+
                            Util.byteArrayToString(toBytes(new String(
                            lbc.convertAll(usm94)))));
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    static byte[] toBytes(String s) {
        byte b[]=new byte[2*s.length()];
        int  i;

        for(i=0; i<s.length(); i++) {
            b[i*2]=(byte)(s.charAt(i)>>8);
            b[i*2+1]=(byte)(s.charAt(i)%256);
        }
        return b;
    }
}
