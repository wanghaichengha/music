package com.example.music.controller;

import com.example.music.mapper.LoveMusicMapper;
import com.example.music.mapper.MusicMapper;
import com.example.music.model.Music;
import com.example.music.model.User;
import com.example.music.tools.Constant;
import com.example.music.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/music")
public class MusicController {

    @Value("${music.local.path}")
    private String SAVE_PATH;

    @Autowired
    private LoveMusicMapper loveMusicMapper;

    @Autowired
    private MusicMapper musicMapper;

    @RequestMapping("/upload")
    public ResponseBodyMessage<Boolean> selectMusic(@RequestParam String singer ,
                                                    @RequestParam("filename") MultipartFile file,
                                                    HttpServletRequest request,
                                                     HttpServletResponse response) throws IOException {
      //一，通过会话判断用户是否已经登录，才能上传歌曲
      HttpSession httpSession= request.getSession(false);
      if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY)==null){
          System.out.println("用户未登录！");
          return new ResponseBodyMessage<>(-1,"用户未登录！",false);
      }
      //二，判断上传的文件是否是音频文件
      //获取文件的名称和类型
      String fileNameAndType = file.getOriginalFilename();
      System.out.println("文件名称："+ fileNameAndType);
      //1,准备歌曲名称
      int index = fileNameAndType.lastIndexOf(".");
      String title = fileNameAndType.substring(0,index);
      //三，检查是否歌曲已存在
      Music music = musicMapper.selectMusicByTitle(title,singer);
      if (music != null){
          return new ResponseBodyMessage<>(-1,"上传失败,歌曲已存在！",false);
      }
      //拼接上传文件本地保存地址
      String path = SAVE_PATH + fileNameAndType;
      System.out.println("文件路径："+path);
      File dest = new File(path);
      System.out.println("dest:=>" + dest.getPath());
      if (!dest.exists()) {
          dest.mkdir();
      }
      //上传文件到本地路径，处理异常
      try {
          file.transferTo(dest);
      } catch (IOException e) {
          e.printStackTrace();
          return new ResponseBodyMessage<>(-1,"服务器上传失败！",false);
      }
      //四，准备数据，将数据上传到数据库
      //2，准备上传歌曲时间,格式化当前系统时间
      SimpleDateFormat timeType = new SimpleDateFormat("HH:mm:ss");
      String time = timeType.format(new Date());
      //3,从会话中获取userid
      User user = (User) httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
      int userid = user.getId();
      //4,播放歌曲的资源路径
      String url = "/music/get?path="+title;
      //把准备好的数据插入到数据库中
      int ret = musicMapper.insert(title,singer,time,url,userid);
      if (ret == 1){
          //后面改为返回一个页面
          response.sendRedirect("/list.html");
          return new ResponseBodyMessage<>(0,"数据库插入成功！",true);
      }else {
          return new ResponseBodyMessage<>(-1,"数据库插入失败！",false);
      }
    }
    //获取歌曲
    @RequestMapping("/get")
    public ResponseEntity<byte[]> get(String path){
        File file = new File(SAVE_PATH+path);
        byte[] a = null;
        try {
            a = Files.readAllBytes(file.toPath());
            if (a == null){
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    //删除歌曲
    @RequestMapping("/delete")
    public ResponseBodyMessage<Boolean> deleteMusicById(@RequestParam String id){
        int iid = Integer.parseInt(id);
        //判断音乐是否存在
        Music music = musicMapper.selectMusicById(iid);
        if (music == null){
            return new ResponseBodyMessage<>(-1,"未找到歌曲，删除失败！",false);
        }
        //删除音乐，返回影响行数
        int ret = musicMapper.deleteMusicById(iid);
        if (ret == 1){
            //数据库歌曲删除成功，删除服务器存储歌曲
            int index = music.getUrl().indexOf("=");
            String filename = music.getUrl().substring(index+1);
            File file = new File(SAVE_PATH+filename+".mp3");
            System.out.println("歌曲路径："+file.getPath());
            if (file.delete()){
                //删除服务器的音乐文件后，删除喜欢音乐列表的音乐
                loveMusicMapper.deleteLoveMusicById(iid);
                return new ResponseBodyMessage<>(0,"服务器歌曲删除成功！",true);
            }else {
                return new ResponseBodyMessage<>(-1,"服务器歌曲删除失败！",false);
            }
        }else {
            return new ResponseBodyMessage<>(-1,"数据库歌曲删除失败！",false);
        }
    }

    @RequestMapping("/deleteSel")
    public ResponseBodyMessage<Boolean> deleteSelMusic(@RequestParam("id[]")List<Integer> id) {
        int sum = 0;
        for (int i = 0; i < id.size(); i++) {
            //判断音乐是否存在
            Music music = musicMapper.selectMusicById(id.get(i));
            if (music == null) {
                return new ResponseBodyMessage<>(-1, "未找到歌曲，删除失败！", false);
            }
            int index = music.getUrl().indexOf("=");
            String filename = music.getUrl().substring(index + 1);
            File file = new File(SAVE_PATH + filename + ".mp3");
            int ret = musicMapper.deleteMusicById(id.get(i));
            if (ret == 1) {
                //数据库歌曲删除成功！
                //删除服务器歌曲
                if (file.delete()) {
                    //删除服务器的音乐文件后，删除喜欢音乐列表的音乐
                    loveMusicMapper.deleteLoveMusicById(id.get(i));
                    sum += ret;
                } else {
                    return new ResponseBodyMessage<>(-1, "服务器删除歌曲失败！", false);
                }
            } else {
                return new ResponseBodyMessage<>(-1, "数据库歌曲删除失败！", false);
            }
        }
        if (sum == id.size()) {
            System.out.println("删除成功！");
            return new ResponseBodyMessage<>(0, "批量删除歌曲成功！", true);
        } else {
            System.out.println("删除失败！");
            return new ResponseBodyMessage<>(-1, "批量删除歌曲失败！", false);
        }
    }

    //查找歌曲
    @RequestMapping("/findmusic")
    public ResponseBodyMessage<List<Music>> findMusic(@RequestParam(required = false) String musicName){
        List<Music> musicList = null;
        System.out.println(musicName);
        if (musicName == null){
            //默认查询所有的歌曲
            musicList = musicMapper.findMusic();
        }else {
            musicList = musicMapper.findMusicByName(musicName);
        }
        return new ResponseBodyMessage<>(0,"查询到的歌曲",musicList);
    }

}
