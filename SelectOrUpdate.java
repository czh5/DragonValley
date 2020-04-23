import java.sql.*;
import java.util.*;

public class SelectOrUpdate {//查询个人信息和修改个人信息
    public static void main(String[] args) {
        int id = 3;//通过id锁定用户
        System.out.println("输入“查询个人信息”可查看个人信息");
        String scan = new Scanner(System.in).nextLine();
        if("查询个人信息".equals(scan)){
            select(id);
        }else{
            System.out.println("无效操作");
        }
    }

    private static void select(int id) {
        Connection con = null;
        PreparedStatement pre = null;
        ResultSet res = null;
        String sql;

        try {
            con = Method.getConnection();//注册驱动+获取连接

            sql = "select a.loginName,a.loginPassword,b.phone from users a join userinformation b on a.id=b.id where a.id=?";//给定账户可以查询信息
            pre = con.prepareStatement(sql);
            pre.setInt(1,id);
            res = pre.executeQuery();
            if(res.next()){
                System.out.println("用户名："+res.getString("loginName"));
                System.out.println("密码："+res.getString("loginPassword"));
                System.out.println("手机号："+res.getString("phone"));
            }

            //修改
            System.out.println("(输入“修改”可以更改信息)");
            String scan = new Scanner(System.in).nextLine();
            if("修改".equals(scan)){
                String loginName = res.getString("loginName");
                String loginPassword = res.getString("loginPassword");
                String phone = res.getString("phone");

                System.out.println("请输入要修改的信息：");
                scan = new Scanner(System.in).nextLine();
                //判断是否正确输入要修改的类型
                if(!"用户名".equals(scan) && !"密码".equals(scan) && !"手机号".equals(scan)){
                    System.out.println("非法输入");
                    select(id);
                }else {
                    update(loginName, loginPassword, phone, scan,id);
                }

            }else{
                return;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Method.close(con,pre,res);
        }
    }

    private static void update(String loginName, String loginPassword, String phone, String scan,int id) {
        System.out.println("请输入新的"+scan+"：");
        String update = new Scanner(System.in).nextLine();

        if("用户名".equals(scan)){
            loginName = update;
        }else if("密码".equals(scan)){
            loginPassword = update;
        }else{
            phone = update;
        }
        change(loginName,loginPassword,phone,id);//修改信息
        select(id);//把修改后的信息重新展示
    }

    private static void change(String loginName, String loginPassword, String phone,int id) {
        Connection con = null;
        PreparedStatement pre = null;
        try {
            con = Method.getConnection();

            String sql = "update users a join userinformation b on a.id=b.id set a.loginName=?,a.loginPassword=?,b.phone=? where a.id=?";//更改信息
            pre = con.prepareStatement(sql);
            pre.setString(1,loginName);
            pre.setString(2,loginPassword);
            pre.setString(3,phone);
            pre.setInt(4,id);

            pre.executeUpdate();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Method.close(con,pre,null);
        }

        System.out.println("修改成功");
    }


}
