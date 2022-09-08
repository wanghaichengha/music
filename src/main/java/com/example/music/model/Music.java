package com.example.music.model;

import lombok.Data;

@Data
public class Music {
    private int id;
    private String title;
    private String singer;
    private String url;
    private String time;
    private int userid;
}
