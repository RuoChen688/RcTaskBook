package RcTaskBook.book;

import RcTaskBook.Main;
import cn.nukkit.Player;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Book {

    private Config config;

    private String name;

    private String showName;

    private String author;

    private ArrayList<String> context;

    public Book(String name,Config config){
        this.name = name;
        this.config = config;
    }

    public static Book loadBook(String name,Config config){
        Book book = new Book(name,config);
        book.setShowName(config.getString("显示名称"));
        book.setAuthor(config.getString("签名"));
        ArrayList<String> list = (ArrayList<String>) config.getStringList("内容");
        for(int i = 0;i < list.size();i++){
            String s = list.get(i);
            if(s.contains("@n")) s = s.replace("@n","\n");
            list.set(i,s);
        }
        book.setContext(list);

        return book;
    }

    public static void giveBook(Player player,String name,int count){
        if(!Main.instance.loadBook.containsKey(name)) return;
        Book book = Main.instance.loadBook.get(name);
        ItemBookWritten item = new ItemBookWritten(0,count);
        CompoundTag tag = item.getNamedTag();
        if(tag == null){
            tag = new CompoundTag();
        }
        tag.putString("name",name);
        item.setNamedTag(tag);
        item.writeBook(book.getAuthor(),book.getShowName(),book.getContext().toArray(new String[0]));
        player.getInventory().addItem(item);
    }

}
