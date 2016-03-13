package com.example.lenovo.myfile;

import java.io.File;
import java.util.Comparator;

/**
 * Created by lenovo on 2015/11/18.
 */
public class CompareByTime implements Comparator {

    Long time,time2;

    @Override
    public int compare(Object obj1, Object obj2) {

        File f1 = (File) obj1;
        File f2 = (File) obj2;

        time = f1.lastModified();
        time2 = f1.lastModified();
        return time.compareTo(time2);
    }
}
