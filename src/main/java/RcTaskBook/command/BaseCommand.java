package RcTaskBook.command;

import RcTaskBook.Main;
import RcTaskBook.book.Book;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BaseCommand extends PluginCommand {

    public BaseCommand(){
        super("book","任务书插件",Main.instance);
    }

    public boolean execute(CommandSender commandSender, String commandLabel, String[] strings) {
        if(!(commandSender instanceof Player)) return false;
        if(!commandSender.isOp()){
            commandSender.sendMessage("权限不足");
            return false;
        }
        Player sender = Server.getInstance().getPlayer(commandSender.getName());
        List<String> args = Arrays.asList(strings);
        switch (args.get(0)) {
            case "help" -> {
                sender.sendMessage("/book add [名称] 创建一个名为[名称]的任务书");
                sender.sendMessage("/book del [名称] 删除一个名为[名称]的任务书");
                sender.sendMessage("/book give [玩家] [名称] [数量] 给予玩家一定数量的任务书");
            }
            case "add" -> {
                if(args.size() != 2){
                    sender.sendMessage("参数错误");
                    return false;
                }
                String name = args.get(1);
                if(Main.instance.loadBook.containsKey(name)){
                    sender.sendMessage("已经存在相同名称的任务书");
                    return false;
                }
                Main.instance.saveResource("book.yml", "/Book/" + name + ".yml", false);
                Config config = new Config(Main.instance.getDataFolder() + "/Book/" + name + ".yml",Config.YAML);
                config.set("名称",name);
                Book book = Book.loadBook(name,config);
                Main.instance.loadBook.put(name,book);
                sender.sendMessage("添加成功");
            }
            case "del" -> {
                if(args.size() != 2){
                    sender.sendMessage("参数错误");
                    return false;
                }
                String name = args.get(1);
                if(!Main.instance.loadBook.containsKey(name)){
                    sender.sendMessage("不存在该名称的任务书");
                    return false;
                }
                File file = new File(Main.instance.getDataFolder() + "/Book/" + name + ".yml");
                file.delete();
                Main.instance.loadBook.remove(name);
                sender.sendMessage("删除成功");
            }
            case "give" -> {
                if(args.size() != 4){
                    sender.sendMessage("参数错误");
                    return false;
                }
                String name = args.get(2);
                if(!Main.instance.loadBook.containsKey(name)){
                    sender.sendMessage("不存在该名称的任务书");
                    return false;
                }
                String pName = args.get(1);
                if(!Server.getInstance().getPlayer(pName).isOnline()){
                    sender.sendMessage("该玩家未在线，无法给予任务书");
                    return false;
                }
                Book.giveBook(Server.getInstance().getPlayer(pName), name, Integer.parseInt(args.get(3)));
            }
        }
        return true;
    }

}
