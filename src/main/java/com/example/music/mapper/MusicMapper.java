package com.example.music.mapper;

import com.example.music.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MusicMapper {

    //插入音乐
    int insert (String title,String singer,String time,String url,int userid);
    //根据singer和title搜索歌曲
    Music selectMusicByTitle (String title,String singer);
    //根据歌曲id删除音乐
    int deleteMusicById(int musicId);
    //根据歌曲id查找音乐
    Music selectMusicById(int musicId);
    //根据歌曲名称，查找歌曲
    List<Music> findMusicByName(String musicName);
    //查找所有歌曲
    List<Music> findMusic();

}
