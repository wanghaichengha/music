package com.example.music.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleDateFormatTest {
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss");
    String time1 = simpleDateFormat1.format(new Date());

    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    String time2 = simpleDateFormat2.format(new Date());
}

