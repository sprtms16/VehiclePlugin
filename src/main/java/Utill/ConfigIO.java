package Utill;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigIO {// 클래스 명
   JavaPlugin Main;// 초기화시 메인클래스를 매개변수로 넣어 서버관련 메소드를 호출 할 수 있게 한다.
   String mConfigPath;// 인스턴스 초기화 시 각 기능에 맞게 호출되어서 어디에 저장할 지 만들어 둡니다.
   Player mPlayer;// Player변수를 저장하는 변수
   AsyncPlayerChatEvent mChat; 
   ItemStack mFood; // 아이템 관련 변수
   Map<String, Object> map = new HashMap<String, Object>();// HashMap을 이용하여 Json형식으로 변환합니다.
   File file;// 파일 관련 변수

   public ConfigIO(JavaPlugin Main, Player mPlayer) {// player관련 용으로 사용할때 호출하는 생성자 Player를 초기화 하고 저장위치를 Player라는 폴더에 각
                                          // 플레이어의 UUID로 이루어진 폴더를 만들고 그 안에 config.json파일을 생성한다.
      // TODO Auto-generated constructor stub
      this.Main = Main;
      this.mPlayer = mPlayer;
      mConfigPath = "Player" + File.separator + mPlayer.getUniqueId().toString();
      init();
   }

   public ConfigIO(JavaPlugin Main, AsyncPlayerChatEvent mChat) {
                                                
      // TODO Auto-generated constructor stub
      this.Main = Main;
      this.mChat = mChat;
      mConfigPath = "Swaer";
      init();
   }

   public ConfigIO(JavaPlugin Main, ItemStack mFood) {// LivingEntity관련용 으로 mFood를 초기화 하고 저장위치를 Food라는 폴더에
                                          // config.json파일로 저장한다.
      // TODO Auto-generated constructor stub
      this.Main = Main;
      this.mFood = mFood;
      mConfigPath = "Food";
      init();
   }

   public ConfigIO add(String key, Object value) {// map에 저장을 한 뒤 연속해서 이어붙일 수 있도록 자기 자신을 호출해 준다.
      map.put(key, value);
      return this;
   }// 사용법 : COnfigIO configio = new COnfigIO(this,player);
      // configio.add("Health",player.getHealth()).add("name",player.getDisplayName()).commit();

   public void commit() {// HashMap으로 되어있는 형식을 Json형식으로 변환시켜 지정한 Path에 저장한다.
      Gson gson = new Gson();

      try {
         FileWriter fileWriter = new FileWriter(file);// FileWriter로 저장한다.
         fileWriter.write(gson.toJson(map));
         fileWriter.flush();
         fileWriter.close();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   public JsonObject getConfig() {//path에서 파일을 읽어 String형태의 값을 Json형태로 반환한다.
      JsonObject jsonObject = null;
      if (file.exists()) {
         JsonParser jsonParser = new JsonParser();
         jsonObject = jsonParser.parse(readJsonFile(file)).getAsJsonObject();
      }
      return jsonObject;

   }

   private void init() {// 지정한 FilePath를 file변수에 지정해 주고 파일을 생성해둔다.
      file = mWriteFile(Main.getDataFolder().getAbsolutePath() + File.separator + mConfigPath + File.separator,
            "config.json");

   }

   private static String readJsonFile(File path) {//getConfig를 할 떄 지정된 path에서 파일을 읽어 그 값을 String형태로 출력해 준다. 
      StringBuffer sb = null;
      try {
         sb = readTextInFile(path);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return sb.toString();
   }

   private static StringBuffer readTextInFile(File file_path) throws IOException {//지정된 Path에서 파일을 읽는 상세공정
      int i;
      StringBuffer full_text = new StringBuffer();
      FileReader in = null;
      try {
         in = new FileReader(file_path);
         while ((i = in.read()) != -1) {//라인을 읽고 그 라인이 더이상 읽을 것이 없을 떄 까지 읽어 StringBuffer에 저장한다. 
            char ch = (char) i;
            full_text.append(ch);
         }
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         in.close();
      }

      return full_text;
   }


   private File mWriteFile(String DirPath, String FileName) {//파일을 기록한다. 지정한 장소에 지정한 파일 이름으로 저장한다. 
      File dir = new File(DirPath);
      if (!dir.exists()) {//만일 폴더가 없을 경우 
         dir.mkdirs();//폴더를 생성한다. 
      }
      File file = new File(DirPath + File.separator + FileName);
      try {
         if (!file.exists())//만일 파일이 없을 경우 파일을 생성한다. 
            file.createNewFile();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return file;//생성된 파일을 반환한다. 
   }
}