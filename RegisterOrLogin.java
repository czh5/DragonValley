import java.sql.*;
import java.util.*;

public class RegisterOrLogin {//登陆和注册
    public static void main(String[] args) {
        System.out.println("请输入登陆还是注册");
        Scanner scanner = new Scanner(System.in);
        String choose =scanner.nextLine();//选择登陆或是注册

        if("登陆".equals(choose)){
            System.out.println(login() ? "登陆成功":"账号或密码错误，请重试");
        }else if("注册".equals(choose)){
            System.out.println(register() ? "注册成功，请登陆":"账户已存在，请登陆");
        }else{
            System.out.println("请重新输入");
        }
    }

    private static boolean register() {
        //填入个人信息
        Scanner scan = new Scanner(System.in);
        System.out.println("请输入你的信息：");
        System.out.println("身份证号码：");
        String idNum = scan.nextLine();
        System.out.println("联系电话：");
        String phone = scan.nextLine();
        System.out.println("用户id：");
        String userId = scan.nextLine();

        if(Method.ifExist(idNum,phone,userId)){//验证账户是否已经存在,如果返回true说明账户已存在，结束注册
            return false;
        }

        Connection con =null;
        PreparedStatement pre = null;
        ResultSet res = null;
        String sql;
        String loginName;
        try {
            con = Method.getConnection();//注册驱动+获取连接

            System.out.println("请创建你的账号名：");
            while(true) {//检验输入的账号是否有重复
                loginName = new Scanner(System.in).nextLine();
                sql = "select * from users where loginName = ?";
                pre = con.prepareStatement(sql);
                pre.setString(1,loginName);
                res = pre.executeQuery();

                if(res.next()){
                    System.out.println("该账户名已被占用，请重试");
                }else{
                    break;//此时创建的账号名已没有重复
                }
            }
            System.out.println("请输入你的密码：");
            String loginPassword = new Scanner(System.in).nextLine();

            sql = "insert into users(loginName,loginPassword) values(?,?)";
            pre = con.prepareStatement(sql);
            pre.setString(1,loginName);
            pre.setString(2,loginPassword);
            pre.executeUpdate();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {//释放资源
            Method.close(con,pre,res);
        }
        return true;
    }

    private static boolean login(){
        System.out.println("用户名：");
        String loginName = new Scanner(System.in).nextLine();
        System.out.println("密码：");
        String loginPassword = new Scanner(System.in).nextLine();

        Map<String,String> login = new HashMap<>();
        Connection con = null;
        PreparedStatement pre = null;
        ResultSet res = null;
        boolean ifs = false;//即ifsuccessful，判断用户输入是否与数据库中信息对应

        try {
            con = Method.getConnection();//注册驱动+获取连接
            //获取预编译的数据库对象
            String sql = "select * from users where loginName = ? and loginPassword = ?";
            pre = con.prepareStatement(sql);
            pre.setString(1,loginName);//将loginName填入第一个占位符中
            pre.setString(2,loginPassword);//将loginPassword填入第二个占位符中
            //执行sql
            res = pre.executeQuery();
            //处理结果集
            if(res.next()){
                ifs= true;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //释放资源
            Method.close(con,pre,res);
        }
        return ifs;
    }
}
