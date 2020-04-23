import java.sql.*;
import java.util.*;

public class ManageManager {//管理驯龙高手
    public static void main(String[] args) {
        System.out.println("输入“查询驯龙高手信息”可查询部落信息");
        String scan = new Scanner(System.in).nextLine();
        if("查询驯龙高手信息".equals(scan)){
            selectManager();
        }else{
            System.out.println("非法操作");
        }
    }

    private static void selectManager() {//默认查询全部驯龙高手的信息
        Connection con = null;
        PreparedStatement pre = null;
        ResultSet res = null;
        String sql;
        Map<String,Integer> index = new HashMap<>();

        try {
            con = Method.getConnection();
            boolean ifContinue;//用来判断查询操作是否继续
            String choose;

            do {//循环是为了反复查询
                sql = "select a.* from userInformation a join users b on a.id=b.id where b.root=?";
                pre = con.prepareStatement(sql);
                pre.setString(1, "Manager");
                res = pre.executeQuery();

                while (res.next()) {
                    System.out.println("驯龙高手的编号：" + res.getString("id"));
                    System.out.println("驯龙高手的身份证号：：" + res.getString("idNum"));
                    System.out.println("驯龙高手的联系电话：" + res.getString("phone"));
                    System.out.println("驯龙高手的用户编号：" + res.getString("userId"));
                    System.out.println("");
                }

                System.out.println("输入“聘用驯龙高手”可新增驯龙高手，输入“开除驯龙高手”可开除驯龙高手，输入“修改驯龙高手信息”可更改驯龙高手信息,输入“退出”可退出查询");
                choose = new Scanner(System.in).nextLine();
                if("聘用驯龙高手".equals(choose)){
                    insertManager(con,pre,res);
                    ifContinue = true;
                }else if("开除驯龙高手".equals(choose)){
                    System.out.println("请输入你要开除的驯龙高手的编号：");
                    String deleteNum = new Scanner(System.in).nextLine();
                    index.put("id",Integer.valueOf(deleteNum));//把要删除的编号存入
                    delete(con,pre,index);
                    ifContinue = true;
                }else if("修改驯龙高手信息".equals(choose)){
                    System.out.println("请输入你要修改的驯龙高手的编号：");
                    String id = new Scanner(System.in).nextLine();
                    index.put("id",Integer.valueOf(id));//把编号存入
                    ifContinue = update(con, pre, index);
                }else if("退出".equals(choose)){
                    ifContinue = false;
                }else{
                    System.out.println("非法操作");
                    ifContinue = false;
                }
            }while(ifContinue);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Method.close(con,pre,res);
        }
    }

    //聘用驯龙高手
    private static void insertManager(Connection con, PreparedStatement pre, ResultSet res) throws SQLException {
        //先向园长展示酒馆内的所有可选人员名单
        String sql = "select * from tavern";
        pre = con.prepareStatement(sql);
        res = pre.executeQuery();
        System.out.println("可选名单如下");
        while(res.next()){
            System.out.println("编号：" + res.getString("id"));
            System.out.println("身份证号：：" + res.getString("idNum"));
            System.out.println("联系电话：" + res.getString("phone"));
            System.out.println("用户编号：" + res.getString("userId"));
            System.out.println("");
        }
        System.out.println("输入对应编号可聘用相应的人，输入“返回”可返回上一级");
        String choose = new Scanner(System.in).nextLine();
        if("返回".equals(choose)){
            return;
        }

        //把选中的人的权限改为管理员
        String sql2 = "update userInformation a join tavern b on a.`userId`=b.`userId` join users c on a.`id`=c.id set c.root=? where b.id=?";
        pre = con.prepareStatement(sql2);
        pre.setString(1,"Manager");
        pre.setString(2,choose);
        pre.executeUpdate();

        //把选中的人从酒馆中删除
        String sql3 = "delete from tavern where id=?";
        pre = con.prepareStatement(sql3);
        pre.setString(1,choose);
        pre.executeUpdate();

        return;
    }

    //开除驯龙高手
    private static void delete(Connection con, PreparedStatement pre, Map<String, Integer> index) throws SQLException {
        //删除驯龙高手则要处理该驯龙高手管理的部落
        System.out.println("请输入该驯龙高手管理的部落应该交由用户编号是什么的驯龙高手");
        String newManagerId = new Scanner(System.in).nextLine();
        String sql = "update exhibitionHall a join userInformation b on a.managerId=b.userId set a.managerId=? where b.id=? ";
        pre = con.prepareStatement(sql);
        pre.setString(1,newManagerId);//将部落管理员改为指定驯龙高手
        pre.setInt(2,index.get("id"));
        pre.executeUpdate();

        //处理完部落再处理驯龙高手的登陆信息和用户信息
        sql = "delete from userInformation where id=?";//用户信息
        pre = con.prepareStatement(sql);
        pre.setInt(1,index.get("id"));
        pre.executeUpdate();

        sql = "delete from users where id=?";//用户信息
        pre = con.prepareStatement(sql);
        pre.setInt(1,index.get("id"));
        pre.executeUpdate();//登陆信息

        System.out.println("删除成功");
    }

    //修改驯龙高手信息
    private static boolean update(Connection con, PreparedStatement pre, Map<String,Integer> index) throws SQLException {
        boolean ifContinue = true;
        System.out.println("请输入要修改的类型：");
        String choose = new Scanner(System.in).nextLine();
        System.out.println("请输入更改后的内容：");
        String newData = new Scanner(System.in).nextLine();
        String type;//用来接收类型
        String sql;

        if("驯龙高手的编号".equals(choose)){
            type = "id";
        }else if("驯龙高手的身份证号".equals(choose)){
            type = "idNum";
        }else if("驯龙高手的联系电话".equals(choose)){
            type = "phone";
        }else if("驯龙高手的用户编号".equals(choose)){
            type = "userId";
        }else{
            System.out.println("非法操作");
            return false;
        }

        if(type == "id"){//如果修改用户信息中的id，则需要和用户登陆的id同步改变
            sql = "update users set "+type+"=? where id=?";
            pre = con.prepareStatement(sql);
            pre.setString(1,newData);
            pre.setInt(2,index.get("id"));//驯龙高手的编号
            pre.executeUpdate();
        }else if(type == "userId"){//如果修改用户编号，那么对应部落的管理员编号也要同步更改
            sql ="update userinformation a join exhibitionHall b on a.userId=b.managerId set b.managerId=? where a.id=?";
            pre = con.prepareStatement(sql);
            pre.setString(1,newData);
            pre.setInt(2,index.get("id"));//驯龙高手的编号
            pre.executeUpdate();
        }

        sql = "update userInformation set "+type+"=? where id=?";
        pre = con.prepareStatement(sql);
        pre.setString(1,newData);
        pre.setInt(2,index.get("id"));//驯龙高手的编号
        pre.executeUpdate();


        return ifContinue;
    }
}
