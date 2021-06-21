// Yichao Qiu | yiq23 | Assignment 5
import java.util.*;

public class Add128 implements SymCipher {

    private byte[] bArray;

    int size = 128;

    //add constructors

    public Add128() {

        Random r = new Random();
        bArray = new byte[size];
        r.nextBytes(bArray);
    }

    public Add128(byte[] arr) {
        // check if its the right size
        if (arr.length != size) {
            throw new IllegalArgumentException("Invalid size parameter");

        }
        bArray = new byte[size];
        this.bArray = arr;
    }


    @Override
    public String decode(byte[] bytes) {
        byte[] de = bytes.clone();
        //store the bytes
        for (int i = 0; i < de.length; i++)
        {
            int x = i % bArray.length;
            // trans the int to byte
            de[i] = (byte) (de[i] - bArray[x]);

        }

        return new String(de);
    }

    @Override
    public byte[] encode(String S) {
        byte[] en = S.getBytes();
        //store the bytes
        for (int i = 0; i < en.length; i++)
        {
            int x = i % bArray.length;
            // trans the int to byte
            en[i] = (byte) (en[i] + bArray[x]);

        }
        return en;
    }

    @Override
    public byte[] getKey() {
        byte[] arr = bArray;
        return arr;
    }

}