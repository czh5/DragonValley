import java.sql.*;
import java.util.*;

public class ManageTribeAndDragon {//管理部落和龙(驯龙高手)
    public static void main(String[] args) {
        String loginName = "Manager01";//通过用户名锁定用户
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
        Map<String,Integer> index = new HashMap<>();//用来存放部落编号


        try {
            con = Method.getConnection();
            //先通过用户名确定该管理员对应的部落编号
            sql = "select b.id from userInformation a join exhibitionHall b on a.userId=b.managerId where a.userId=?";
            pre = con.prepareStatement(sql);
            pre.setString(1,loginName);
            res = pre.executeQuery();
            boolean ifContinue = false;//用来判断查询操作是否继续

            if(res.next()){
                int hallId =Integer.valueOf(res.getString("id"));//得到查询到的展厅编号
                index.put("id",hallId);
                do {
                    ifContinue =  Method.selectTribe(con, pre, res, index, loginName);//通过编号查询对应部落信息
                }while(ifContinue);//当返回true时说明继续查询
            }else{
                System.out.println("操作错误");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Method.close(con,pre,res);
        }
    }
}
