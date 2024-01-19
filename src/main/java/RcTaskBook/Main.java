package RcTaskBook;

import RcTaskBook.book.Book;
import RcTaskBook.command.BaseCommand;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Main extends PluginBase implements Listener {

    public static Main instance;

    public LinkedHashMap<String, Book> loadBook = new LinkedHashMap<>();

    public Config config;

    @Override
    public void onEnable(){
        instance = this;
        this.getServer().getPluginManager().registerEvents(this,this);
        this.getServer().getCommandMap().register("book",new BaseCommand());
        this.saveResource("config.yml","/Config.yml",false);
        this.config = new Config(this.getDataFolder() + "/Config.yml",Config.YAML);
        File bookFile = this.getBookFile();
        if(!bookFile.exists() && !bookFile.mkdirs()){
            this.getLogger().info("Book文件夹创建失败");
        }
        this.getLogger().info("开始读取任务书配置文件");
        for(String name: getDefaultFiles("Book")){
            this.getLogger().info("读取 "+name+".yml");
            Book book;
            try {
                book = Book.loadBook(name,new Config(this.getDataFolder() + "/Book/"+name+".yml",Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.loadBook.put(name,book);
            this.getLogger().info(name+"任务书配置读取成功");
        }
        this.getLogger().info("插件加载成功");
    }

    public File getBookFile(){
        return new File(this.getDataFolder() + "/Book");
    }

    public static String[] getDefaultFiles(String fileName) {
        List<String> names = new ArrayList<>();
        File files = new File(Main.instance.getDataFolder()+ "/"+fileName);
        if(files.isDirectory()){
            File[] filesArray = files.listFiles();
            if(filesArray != null){
                for (File file : filesArray) {
                    names.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
                }
            }
        }
        return names.toArray(new String[0]);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(this.config.getBoolean("自动修正")){
            for(int i = 0;i < player.getInventory().getSize();i++){
                Item item = player.getInventory().getItem(i);
                if(item instanceof ItemBookWritten && item.getNamedTag().contains("name")){
                    Book book = Main.instance.loadBook.get(item.getNamedTag().getString("name"));
                    ((ItemBookWritten) item).writeBook(book.getAuthor(), book.getShowName(),book.getContext().toArray(new String[0]));
                    player.getInventory().setItem(i,item);
                }
            }
        }
    }

}
