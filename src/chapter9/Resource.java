package chapter9;

import java.io.*;

public class Resource {
    static void copyV1(String s, String dst) throws IOException{
        InputStream in=new FileInputStream(s);
        try{
            OutputStream out=new FileOutputStream(dst);
            try {
                byte[] buf=new byte[100];
                int n;
                while ((n = in.read(buf)) >= 0){
                    out.write(buf,0,n);
                }
            }finally {
                out.close();
            }
        }finally {
            in.close();
        }
    }

    static void copyV2(String s, String dst) throws IOException{
        try(InputStream in=new FileInputStream(s);OutputStream out=new FileOutputStream(dst)) {
            byte[] buf = new byte[100];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
        }
    }
}
