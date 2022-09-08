package com.example.music.mapper;

import com.example.music.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoveMusicMapper {

    //在喜欢歌曲表中查询是否存在
    Music findLoveMusicByMusicIdAndUserId(int userid, int musicid);
    //是否插入成功
    boolean insertLoveMusic(int userid, int musicid);
    //查询用户id自己喜欢的音乐, 如果没有传入具体的歌曲名，显示当前用户收藏的所有音乐
    List<Music>  findLoveMusicByUserId(int userid);
    // 根据某个用户的ID和歌曲名称查询，某个用户收藏的音乐
    List<Music> findLoveMusicBykeyAndUID(String musicName, int userid);
    //根据用户id和音乐id移除收藏的歌曲
    int deleteLoveMusic(int userid,int musicid);
    //根据音乐id移除音乐
    int deleteLoveMusicById(int musicid);


}
