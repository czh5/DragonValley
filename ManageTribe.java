import java.sql.*;
import java.util.*;

public class ManageTribe {//管理部落(园长)
    public static void main(String[] args) {
        String loginName = "DragonMom";//存放用户名，用来判断权限
        System.out.println("输入“查询部落信息”可查询部落信息");
        String scan = new Scanner(System.in).nextLine();
        if("查询部落信息".equals(scan)){
            selectTribe(loginName);
        }else{
            System.out.println("非法操作");
        }
    }

    private static void selectTribe(String loginName) {
        Connection con = null;
        PreparedStatement pre = null;
        ResultSet res = null;
        Map<String,Integer> index = new HashMap<>();//用来存放更改后的新部落编号
        boolean ifContinue = false;
        try {
            con = Method.getConnection();
            do{
                ifContinue = Method.selectTribe(con,pre,res,index,loginName);//查询部落
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
