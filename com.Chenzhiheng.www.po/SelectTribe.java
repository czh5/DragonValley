import java.sql.*;
import java.util.*;

public class SelectTribe {//查询部落(外邦人)
    public static void main(String[] args) {
        String loginName = "zhangsan";//通过用户名锁定用户
        System.out.println("输入“查询部落信息”可查看部落信息");
        String scan = new Scanner(System.in).nextLine();

        if("查询部落信息".equals(scan)){
            selectTribe(loginName);
        }else{
            System.out.println("无效操作");
        }
    }

    private static void selectTribe(String loginName) {
        Connection con = null;
        PreparedStatement pre = null;
        ResultSet res = null;
        String sql;
        String tribeId;//部落编号
        Map<String,Integer> index = new HashMap<>();//用来存放查询的部落编号
        boolean ifContinue = false;

        try {
            con = Method.getConnection();//注册驱动+获取连接
            do {
                System.out.println("请输入要查询的部落的编号：");
                tribeId = new Scanner(System.in).nextLine();
                index.put("id",Integer.valueOf(tribeId));
                ifContinue = Method.selectTribe(con, pre, res, index,loginName);//当返回false时说明退出查询，返回true时继续查询
            }while(ifContinue);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Method.close(con,pre,res);
        }
    }
}
