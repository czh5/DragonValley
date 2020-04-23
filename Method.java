import java.sql.*;
import java.util.*;


public class Method {
    private Method(){};
    //注册驱动+获取连接
    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        Connection con = null;
        //注册驱动
        Class.forName("com.mysql.jdbc.Driver");
        //获取连接
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dragonvalley","root","czh2001219");
        return con;
    }
    //释放资源
    public static void close(Connection con , Statement sta , ResultSet res){//释放资源
        if(res != null){
            try {
                res.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(sta != null){
            try {
                sta.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //检测账户是否存在(注册)
    public static boolean ifExist(String idNum,String phone,String userId){//注册时判断账户是否已存在，当所填信息与已有信息完全相同时判定为账户已存在
        Connection con = null;
        PreparedStatement pre = null;
        try {
            con = getConnection();//注册驱动+获取连接

            String sql = "select idNum from userInformation where idNum=? and phone=? and userId=?";
            pre = con.prepareStatement(sql);//获取预编译数据库对象

            pre.setString(1,idNum);//填入信息
            pre.setString(2,phone);
            pre.setString(3,userId);

            if(pre.executeQuery().next()){
                return true;//如果能找到一样的说明账户已存在
            }

            sql = "insert into userinformation(idNum,phone,userId) values(?,?,?)";//当确认用户是注册时，在用户表中添加用户信息
            pre = con.prepareStatement(sql);
            pre.setString(1,idNum);//填入信息
            pre.setString(2,phone);
            pre.setString(3,userId);
            pre.executeUpdate();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(con,pre,null);
        }
        return false;
    }
    //查询部落信息(游客+管理员+园长)
    public static boolean selectTribe(Connection con , PreparedStatement pre , ResultSet res, Map<String,Integer> index, String loginName) throws SQLException{//查询部落信息
        String root = selectRoot(con,pre,res,loginName);//查询权限
        if(!"King".equals(root)) {//游客可以查看指定编号部落，管理员可以查看对应部落
            String sql = "select * from exhibitionHall where id=?";
            pre = con.prepareStatement(sql);
            pre.setInt(1, index.get("id"));//填入要查询的部落的编号
            res = pre.executeQuery();
        }else{//当权限为园长时，应当默认查看全部部落
            String sql = "select * from exhibitionHall";
            pre = con.prepareStatement(sql);
            res = pre.executeQuery();
        }

        while(res.next()){//显示部落各项信息,用while是为了防止多部落的现象
            System.out.println("部落编号："+res.getString("id"));
            System.out.println("部落类型："+res.getString("eType"));
            System.out.println("部落简介："+res.getString("introduction"));
            System.out.println("占地面积："+res.getString("eSpace"));
            System.out.println("部落地址："+res.getString("adress"));
            System.out.println("开始时间："+res.getString("startTime"));
            System.out.println("结束时间："+res.getString("endTime"));
            System.out.println("管理员编号："+res.getString("managerId"));
            System.out.println("");
        }

        boolean ifContinue = false;//用来判断是否继续进行查询操作
        if("visitor".equals(root)) {//当权限为游客时，只有查询功能
            System.out.println("输入“查询龙的信息”可查询该部落的龙的信息，输入“查询其他部落”可查询别的部落信息，输入“退出”可退出查询");
            String choose = new Scanner(System.in).nextLine();
            if ("查询龙的信息".equals(choose)) {
                ifContinue = selectDragon(con, pre, res, index.get("id"), loginName,root);//当返回true时说明用户继续查询别的部落，返回false说明用户退出查询
            } else if ("查询其他部落".equals(choose)) {
                ifContinue = true;
            } else if ("退出".equals(choose)) {
            } else {
                System.out.println("非法操作");
            }
        }else if("Manager".equals(root)){//当权限为管理员时，能修改对应部落信息，管理龙
            System.out.println("输入“修改部落信息”可修改部落信息，输入“查询龙的信息”可查询该部落的龙的信息，输入“退出”可结束查询");
            String choose = new Scanner(System.in).nextLine();
            if ("修改部落信息".equals(choose)) {
                updateTribe(con,pre,index);
                ifContinue = true;
            } else if ("查询龙的信息".equals(choose)) {
                do {
                    ifContinue = manageDragon(con, pre, res, index, root, loginName);
                }while(ifContinue);//通过返回值判断是否继续查询龙的信息

                if(index.get("是否回到上一级") == 1){
                    ifContinue = true;
                    index.put("是否回到上一级",0);//将开关调回关闭，防止在后期出现错误，同时也为了防止误判
                }else if(index.get("是否回到上一级") == 2){
                    ifContinue = false;
                    index.put("是否回到上一级",0);//将开关调回关闭，防止在后期出现错误，同时为了防止误判
                }
            } else if ("退出".equals(choose)) {
            } else {
                System.out.println("非法操作");
            }
        }else{//当权限为园长时，可管理部落
            System.out.println("输入“修改部落信息”可修改部落信息，输入“删除部落”可删除部落，输入“新增部落”可添加部落，输入“查询龙的信息”可查询该部落的龙的信息，输入“退出”可结束查询");
            String choose = new Scanner(System.in).nextLine();
            if ("修改部落信息".equals(choose)) {
                System.out.println("请输入你想修改的部落的编号");
                String id = new Scanner(System.in).nextLine();
                index.put("id",Integer.valueOf(id));
                updateTribe(con,pre,index);
                ifContinue = true;
            } else if ("删除部落".equals(choose)){
                System.out.println("请输入你想删除的部落的编号");
                String id = new Scanner(System.in).nextLine();
                index.put("id",Integer.valueOf(id));
                deleteTribe(con,pre,index);
                ifContinue = true;
            }else if ("新增部落".equals(choose)){
                System.out.println("请按“部落类型,部落简介,占地面积,部落地址,管理员编号”的格式输入新增部落的信息");
                String newTribe = new Scanner(System.in).nextLine();
                insertTribe(con,pre,newTribe);
                ifContinue=true;
            }
            else if ("查询龙的信息".equals(choose)) {//园长也应该具有查看和修改龙信息的权限
                System.out.println("请输入你想查询哪个部落的龙");
                String id = new Scanner(System.in).nextLine();
                do {
                    index.put("id",Integer.valueOf(id));
                    ifContinue = manageDragon(con, pre, res, index, root, loginName);
                }while(ifContinue);//通过返回值判断是否继续查询龙的信息

                if(index.get("是否回到上一级") == 1){
                    ifContinue = true;
                    index.put("是否回到上一级",0);//将开关调回关闭，防止在后期出现错误，同时也为了防止误判
                }else if(index.get("是否回到上一级") == 2){
                    ifContinue = false;
                    index.put("是否回到上一级",0);//将开关调回关闭，防止在后期出现错误，同时为了防止误判
                }
            } else if ("退出".equals(choose)) {
            } else {
                System.out.println("非法操作");
            }
        }

        return ifContinue;
    }
    //增添部落
    private static void insertTribe(Connection con, PreparedStatement pre, String newTribe) {
        String sql = "insert exhibitionHall(eType,introduction,eSpace,adress,managerId) values(?,?,?,?,?)";
        try {
            pre = con.prepareStatement(sql);
            pre.setString(1,newTribe.split(",")[0]);
            pre.setString(2,newTribe.split(",")[1]);
            pre.setString(3,newTribe.split(",")[2]);
            pre.setString(4,newTribe.split(",")[3]);
            pre.setString(5,newTribe.split(",")[4]);
            pre.executeUpdate();
            System.out.println("增添成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //删除部落(园长)
    private static void deleteTribe(Connection con, PreparedStatement pre, Map<String, Integer> index) {//删除部落
        String sql;
        try {

            //删除部落则要面临龙怎么处理的问题
            System.out.println("请选择处理龙的方式：输入“遣散”可将原属于该部落的龙遣散，输入“归入其他部落”可将这些龙转移");
            String choose = new Scanner(System.in).nextLine();
            if("归入其他部落".equals(choose)){
                System.out.println("请输入你想将这些龙转移到目标部落的编号");
                String id = new Scanner(System.in).nextLine();

                sql = "update dragon set hallId=? where hallId=?";
                pre = con.prepareStatement(sql);

                pre.setInt(1,Integer.valueOf(id));//要转入的部落的编号
                pre.setInt(2,index.get("id"));//已删除的部落编号，即龙原本所属的部落编号
                pre.executeUpdate();
            }else{
                if(!"遣散".equals(choose)){//如果输入的是非法信息
                    System.out.println("非法操作，默认遣散");
                }
                sql = "delete from dragon where hallId=?";//直接删除
                pre = con.prepareStatement(sql);
                pre.setInt(1,index.get("id"));
                pre.executeUpdate();
            }

            sql = "delete from exhibitionHall where id=?";
            pre = con.prepareStatement(sql);
            pre.setInt(1,index.get("id"));
            pre.executeUpdate();
            System.out.println("删除成功");


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }
    //管理龙(管理员)
    private static boolean manageDragon(Connection con, PreparedStatement pre, ResultSet res, Map<String, Integer> index , String root , String loginName) throws SQLException {
        selectDragon(con,pre,res,index.get("id"),loginName,root);//显示对应部落的龙的信息

        String sql;
        boolean ifContinue = true;
        System.out.println("输入“增加”可以添加龙的信息，输入“删除”可以删除龙的信息，输入“修改”可以修改龙的信息，输入“返回”可返回上一级");
        String choose = new Scanner(System.in).nextLine();

        if("增加".equals(choose)){
            System.out.println("请按“龙的名字，龙的类型，龙的介绍，龙的年龄，龙的健康状况”的格式输入新增的龙的信息");
            String newDragon = new Scanner(System.in).nextLine();
            sql = "insert into dragon(dName,dType,introduction,age,healthLevel,hallId) values(?,?,?,?,?,?)";
            pre = con.prepareStatement(sql);
            pre.setString(1,newDragon.split(",")[0]);//分别填入
            pre.setString(2,newDragon.split(",")[1]);
            pre.setString(3,newDragon.split(",")[2]);
            pre.setString(4,newDragon.split(",")[3]);
            pre.setInt(5,Integer.valueOf(newDragon.split(",")[4]));
            pre.setInt(6,index.get("id"));//管理员不用填对应部落编号，默认放入自己管理的部落中
            pre.executeUpdate();
            System.out.println("增加成功");

        }else if("删除".equals(choose)){
            System.out.println("请输入要删除的龙的编号：");
            String deleteId =new Scanner(System.in).nextLine();

            boolean ifw = true;//即ifwrong，作为判断是否是该部落的龙的依据
            try {//为了防止输入的龙不存在导致报错，结果导致程序结束
                sql = "select hallId from dragon where id=?";//查询要删除的龙对应的部落编号
                pre = con.prepareStatement(sql);
                pre.setInt(1, Integer.valueOf(deleteId));
                res = pre.executeQuery();
                res.next();//能执行完这一步说明这条龙是存在的，但无法确定是不是属于该部落

                if (res.getInt("hallId") == index.get("id")) {//管理员只能删除自己部落的龙
                    sql = "delete from dragon where id=?";
                    pre = con.prepareStatement(sql);
                    pre.setInt(1, Integer.valueOf(deleteId));
                    pre.executeUpdate();
                    System.out.println("删除成功");
                    ifw =false;
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                if(ifw){
                    System.out.println("输入的编号对应的龙不属于本部落或不存在");
                }
            }

        }else if("修改".equals(choose)){
            System.out.println("请输入要修改的类型：");
            String update = new Scanner(System.in).nextLine();
            System.out.println("请输入修改后的值：");
            String newData = new Scanner(System.in).nextLine();
            String type;//用来接收字段

            if("龙的编号".equals(update)){
                type = "id";
            }else if("龙的名字".equals(update)){
                type = "dName";
            }else if("龙的类型".equals(update)){
                type = "dType";
            }else if("龙的介绍".equals(update)){
                type = "introduction";
            }else if("龙的年龄".equals(update)){
                type = "age";
            }else if("龙的健康状况".equals(update)){
                type = "healthLevel";
            }else if("该龙所在部落编号".equals(update)){
                type = "hallId";
            }else{
                System.out.println("非法操作");
                return ifContinue;
            }

            sql = "update dragon set "+type+"=? where hallId=?";
            pre = con.prepareStatement(sql);
            pre.setString(1,newData);
            pre.setInt(2,index.get("id"));
            pre.executeUpdate();
            System.out.println("修改成功");

        }else if("返回".equals(choose)){//返回上一级
            ifContinue = false;
            index.put("是否回到上一级",1);//为了实现和直接退出的差异，加入判定
        }else{
            System.out.println("非法操作");//直接退出查询
            ifContinue = false;//直接返回似乎会出现空指针异常，因此加入以下语句
            index.put("是否回到上一级",2);//为了实现和直接退出的差异，加入判定
        }
        return ifContinue;
    }
    //修改部落信息(管理员+园长)
    private static void updateTribe(Connection con, PreparedStatement pre, Map<String,Integer> index) throws SQLException {
        System.out.println("请输入要修改的类型：");
        String choose = new Scanner(System.in).nextLine();//存放修改的类型
        String type;//由于部落包含的类型较多，如果每个类型都定义一个变量不太实际，因此定义一个变量来接收类型
        System.out.println("请输入更改后的信息：");
        String newdata = new Scanner(System.in).nextLine();//存放新的内容
        String sql;

        if("部落编号".equals(choose)){
            type = "id";
        }else if("部落类型".equals(choose)){
            type = "eType";
        }else if("部落简介".equals(choose)){
            type = "introduction";
        }else if("占地面积".equals(choose)){
            type = "eSpace";
        }else if("部落地址".equals(choose)){
            type = "adress";
        }else if("开始时间".equals(choose)){
            type = "startTime";
        }else if("结束时间".equals(choose)){
            type = "endTime";
        }else if("管理员编号".equals(choose)){
            type = "managerId";
        }else{
            System.out.println("非法操作");
            return;
        }

        if(type == "id"){//如果修改了编号的话，会对部落内的龙有影响，因此要先把龙所属的部落编号也一起改了，如果先改部落再改龙会变得复杂
            sql = "update dragon a join exhibitionHall b on a.hallId=b.id set a.hallId=? where b.id=?";
            pre =con.prepareStatement(sql);
            pre.setInt(1,Integer.valueOf(newdata));
            pre.setInt(2,index.get("id"));//这时部落编号还没改

            pre.executeUpdate();

        }

        sql = "update exhibitionHall a set "+type+"=? where a.id=?";//把type作为sql语句的一部分传入
        pre = con.prepareStatement(sql);
        pre.setString(1,newdata);//修改后的内容
        pre.setInt(2,index.get("id"));//对应部落
        pre.executeUpdate();

        if(type == "id"){
            index.put("id",Integer.valueOf(newdata));
        }

        System.out.println("修改成功");

        return;
    }
    //查询龙的信息(游客+管理员)
    private static boolean selectDragon(Connection con, PreparedStatement pre, ResultSet res, int tribeId,String loginName,String root) {
        boolean ifContinue = false;

        try {
            String sql = "select a.* from dragon a join exhibitionHall b on a.hallId=b.id where b.id=?";
            pre = con.prepareStatement(sql);
            pre.setInt(1,tribeId);
            res = pre.executeQuery();

            while(res.next()){//显示龙的信息  使用while是为了防止有多条龙的现象
                System.out.println("龙的编号："+res.getString("id"));
                System.out.println("龙的名字："+res.getString("dName"));
                System.out.println("龙的类型："+res.getString("dType"));
                System.out.println("龙的介绍："+res.getString("introduction"));
                System.out.println("龙的年龄："+res.getString("age"));

                if(!"visitor".equals(root)) {//当权限高于游客时才显示健康状况
                    System.out.println("龙的健康状况：" + res.getString("healthLevel"));
                }

                System.out.println("该龙所在部落编号："+res.getString("hallId"));
                System.out.println("");
            }

            if("visitor".equals(root)) {//游客可以查询，管理员不可以
                System.out.println("输入“查询其他部落”可查询别的部落信息，输入“退出”可退出查询");
                String choose = new Scanner(System.in).nextLine();
                if ("查询其他部落".equals(choose)) {
                    ifContinue = true;
                } else if ("退出".equals(choose)) {
                    ifContinue = false;
                } else {
                    System.out.println("非法操作");
                    ifContinue = false;
                }
            }
            return ifContinue;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ifContinue;
    }
    //查询权限
    public static String selectRoot(Connection con,PreparedStatement pre,ResultSet res,String loginName){//查询权限
        String root = null;
        String sql = "select root from users where loginName=?";
        try {
            pre = con.prepareStatement(sql);
            pre.setString(1,loginName);
            res = pre.executeQuery();

            if(res.next()) {
                root = res.getString("root");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return root;

    }
}
