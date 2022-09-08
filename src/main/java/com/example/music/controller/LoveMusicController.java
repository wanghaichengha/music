package com.example.music.controller;

import com.example.music.mapper.LoveMusicMapper;
import com.example.music.model.Music;
import com.example.music.model.User;
import com.example.music.tools.Constant;
import com.example.music.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/lovemusic")
public class LoveMusicController {

    @Autowired
    private LoveMusicMapper loveMusicMapper;

    //添加音乐到喜欢的列表
    @RequestMapping("/likemusic")
    public ResponseBodyMessage<Boolean> likeMusic(@RequestParam String id, HttpServletRequest request){
        //将字符串转为整数
        int musicid = Integer.parseInt(id);
        System.out.println("音乐id："+musicid);
        //获取会话，若会话不存在，不新建会话
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USERINFO_SESSION_KEY) == null){
            System.out.println("用户未登录！");
            return new ResponseBodyMessage<>(-1,"用户未登录，添加失败！",false);
        }
        //获取用户，并获取用户id
        User user = (User) session.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();
        System.out.println("用户id："+userid);
        //在喜欢列表中查询歌曲是否已经存在
        Music music = loveMusicMapper.findLoveMusicByMusicIdAndUserId(userid,musicid);
        if (music != null){
            return new ResponseBodyMessage<>(-1,"歌曲已存在，重复添加！",false);
        }
        //插入歌曲
        boolean ret = loveMusicMapper.insertLoveMusic(userid,musicid);
        if (ret){
            return new ResponseBodyMessage<>(0,"添加成功！！",true);
        }else {
            return new ResponseBodyMessage<>(-1,"添加失败！",false);
        }
    }


    //根据用户id和歌曲名查找喜欢的歌曲，支持模糊查询
    @RequestMapping("/findlovemusic")
    public ResponseBodyMessage<List<Music>> findlovemusic (@RequestParam(required = false) String musicName ,
                                                           HttpServletRequest request){
        //判断用户是否已经登录
        HttpSession session = request.getSession(false);
        if (session == null){
            return new ResponseBodyMessage<>(-1,"用户未登录！",null);
        }
        //获取会话，获得用户id
        User user = (User) session.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();
        //查询收藏的歌曲
        List<Music> musicList = new ArrayList<>();
        if (musicName == null){
            musicList = loveMusicMapper.findLoveMusicByUserId(userid);
        }else {
            musicList = loveMusicMapper.findLoveMusicBykeyAndUID(musicName,userid);
        }
        return new ResponseBodyMessage<>(0,"查到了所有收藏的歌曲！",musicList);
    }

    //移除收藏的歌曲
    @RequestMapping("/deletelovemusic")
    public ResponseBodyMessage<Boolean> deleteLoveMusic(@RequestParam String id,HttpServletRequest request){
        int musicid = Integer.parseInt(id);
        //判断用户是否已登录
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USERINFO_SESSION_KEY) == null){
            return new ResponseBodyMessage<>(-1,"用户未登录！",false);
        }
        //获取用户信息
        User user = (User) session.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();
        int ret = loveMusicMapper.deleteLoveMusic(userid,musicid);
        if (ret == 1){
            return new ResponseBodyMessage<>(0,"取消收藏成功！",true);
        }else {
            return new ResponseBodyMessage<>(-1,"取消收藏失败！",false);
        }

    }

}
