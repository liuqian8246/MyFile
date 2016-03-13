package com.example.lenovo.myfile;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by lenovo on 2015/11/17.由大到小
 */
public class CompareBySizeMax implements Comparator{

    File f1,f2;
    int a = 0;
    @Override
    public int compare(Object obj1, Object obj2) {
        f1 = (File) obj1;
        f2 = (File) obj2;

        Long length = getSize(f1);
        Long length2 = getSize(f2);

        return length2.compareTo(length);
    }

    private Long getSize(File f) {
        Long size = 0l;
        if(f.isFile()) {
            size += f.length();
        } else{
            File[] files = f.listFiles();
            for(File file : files) {
                size += getSize(file);
            }

        }
        return size;
    }
}
