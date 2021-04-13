package com.xiaobai.knapsack.controller;

import com.xiaobai.knapsack.pojo.ProfitWeight;
import com.xiaobai.knapsack.utils.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@RestController
public class DealRequest {

    @ResponseBody
    @RequestMapping("/fileupload")
    public JSONObject dealFile(@RequestParam("file") MultipartFile uploadFile) throws IOException, SQLException {
        JSONObject jresp=new JSONObject();
        String filePath="D:/Users/data/";//文件保存的位置
        File saveFile=new File(filePath);
        if (!saveFile.exists()){
            saveFile.mkdirs();
        }
        String finalPath=filePath+uploadFile.getOriginalFilename().trim();
        File finalFile=new File(finalPath);
        if (finalFile.exists()){
            finalFile.delete();
        }
        try {
            uploadFile.transferTo(finalFile);
        } catch (IOException e) {
            System.out.println("文件传输失败");
        }
        InitAnalysis initFile = new InitAnalysis();
        boolean flag=initFile.getAllParameterByFile(finalPath);
        if (flag) {//当前文件是正确的，存入数据库
            writeDataBase(initFile,finalFile);
            jresp.put("data", "0");
        }else
            jresp.put("data", "-1");
        return jresp;
    }

    @RequestMapping("/selectFile")
    public String selectFile() throws SQLException{
        JSONObject json=new JSONObject();
        Connection connection = JDBCUtils.getConnection();
        Statement statement = connection.createStatement();
        String query="SELECT DISTINCT `filename` FROM `knapsack`";
        ResultSet rs=statement.executeQuery(query);
        if (rs.next()){
            json.put("code",0);
            JSONArray array=new JSONArray();
            do {
                array.put(new JSONObject().put("filename",rs.getString("filename")));
            }while (rs.next());
            json.put("data",array);
            json.put("count",array.length());
        }else {
            json.put("code",-1);
        }
        JDBCUtils.release(rs,statement,connection);
        JDBCUtils.recordJournal("选择文件","查询操作");
        return json.toString();
    }

    @RequestMapping("/selectGroup/{filename}")//同时传递参数，用于指定显示的文件的组数
    public String selectGroup(@PathVariable String filename) throws SQLException {
        JSONObject json=new JSONObject();
        //查询数据库，获取键值对，与前端接口相呼应
        Connection connection = JDBCUtils.getConnection();
        Statement statement = connection.createStatement();
        String query="SELECT * FROM `knapsack` WHERE `filename`='"+filename+"'";
        ResultSet rs = statement.executeQuery(query);
        JSONArray array=new JSONArray();
        while(rs.next()){
            JSONObject object=new JSONObject();
            object.put("dimension",rs.getString("dimension"));
            object.put("cubage",rs.getString("cubage"));
            object.put("group",rs.getString("group"));
            object.put("profit",rs.getString("profit"));
            object.put("weight",rs.getString("weight"));
            array.put(object);
        }
        json.put("data",array);
        json.put("code",0);
        json.put("msg","");
        json.put("count",array.length());
        JDBCUtils.release(rs,statement,connection);
        JDBCUtils.recordJournal("选择文件中分组","查询操作");
        return json.toString();
    }

    @RequestMapping("/selectOneGroup/{filename}/{group}")
    public String selectOneGroup(@PathVariable String filename,@PathVariable Integer group) throws SQLException {
        JSONObject json=new JSONObject();
        //查询数据库，获取键值对，与前端接口相呼应
        Connection connection = JDBCUtils.getConnection();
        Statement statement = connection.createStatement();
//        SELECT * FROM `knapsack` WHERE `filename`='idkp1-10.txt' AND `group`='1'
        String query="SELECT * FROM `knapsack` WHERE `filename`='"+filename+"' AND `group`='"+group+"'";
        ResultSet rs = statement.executeQuery(query);
        JSONArray array=new JSONArray();
        if (rs.next()) {
            String[] allProfit = rs.getString("profit").split(",");//获取所有的价值和重量
            String[] allWeight = rs.getString("weight").split(",");
            for (int i = 0; i < allProfit.length; i+=3) {
                JSONObject object=new JSONObject();
                double rate=Double.parseDouble(allProfit[i])/Double.parseDouble(allWeight[i]);
                for (int j = 0; j < 3; j++) {
                    double profit=Double.parseDouble(allProfit[i+j]);
                    double weight=Double.parseDouble(allWeight[i+j]);
                    object.put("profit"+(j+1),profit);
                    object.put("weight"+(j+1),weight);
                }
                object.put("group",(i/3+1));
                object.put("rate",rate);
                array.put(object);
            }
            json.put("count",allProfit.length/3);
        }
        json.put("data",array);
        json.put("code",0);
        json.put("msg","");
        JDBCUtils.release(rs,statement,connection);
        JDBCUtils.recordJournal("选择文件中的一组","查询操作");
        return json.toString();
    }

    @RequestMapping("/scotPicture/{filename}/{group}")
    public String scotPicture(@PathVariable String filename,@PathVariable Integer group) throws SQLException {
        JSONArray json=new JSONArray();
        Connection connection = JDBCUtils.getConnection();
        Statement statement = connection.createStatement();
//        SELECT * FROM `knapsack` WHERE `filename`='idkp1-10.txt' AND `group`='1'
        String query="SELECT * FROM `knapsack` WHERE `filename`='"+filename+"' AND `group`='"+group+"'";
        ResultSet rs = statement.executeQuery(query);
        if (rs.next()){
            String[] allProfit=rs.getString("profit").split(",");
            String[] allWeight=rs.getString("weight").split(",");
            for (int i = 0; i < allProfit.length; i++) {
                JSONObject object=new JSONObject();
                object.put("profit",allProfit[i]);
                object.put("weight",allWeight[i]);
                json.put(object);
            }
        }
        JDBCUtils.release(rs,statement,connection);
        JDBCUtils.recordJournal("选择文件中的一组排序","查询操作");
        return json.toString();
    }

    @RequestMapping("/dynamic/{filename}/{group}")
    public String dynamic(@PathVariable String filename,@PathVariable Integer group) throws SQLException {
        JSONObject json=new JSONObject();
        Connection connection = JDBCUtils.getConnection();
        Statement statement = connection.createStatement();
        String query="SELECT * FROM `knapsack` WHERE `filename`='"+filename+"' AND `group`='"+group+"'";
        ResultSet rs=statement.executeQuery(query);
        JSONArray array=new JSONArray();
        if (rs.next()){
            JSONObject object=new JSONObject();
            String allProfit = rs.getString("profit");
            String allWeight = rs.getString("weight");
            String cubage=rs.getString("cubage");
            String dimension=rs.getString("dimension");
            DynamicProgramming dp=new DynamicProgramming();
            dp.setProfitWeight(new ProfitWeight(dimension,cubage,allProfit,allWeight));
            double time=dp.discountBackpack()/1000.0;
            int maxValue=dp.getOptimalSolution();
            object.put("time",time);
            object.put("group",group);
            object.put("filename",filename);
            object.put("maxvalue",maxValue);
            array.put(object);
        }
        json.put("count",array.length());
        json.put("data",array);
        json.put("code",0);
        json.put("msg","");
        JDBCUtils.release(rs,statement,connection);
        JDBCUtils.recordJournal("动态规划解决最优解","查询操作");
        return json.toString();
    }

    @RequestMapping("/backTrack/{filename}/{group}")
    public String backTrack(@PathVariable String filename,@PathVariable Integer group) throws SQLException {
        JSONObject json=new JSONObject();
        Connection connection = JDBCUtils.getConnection();
        Statement statement = connection.createStatement();
        String query="SELECT * FROM `knapsack` WHERE `filename`='"+filename+"' AND `group`='"+group+"'";
        ResultSet rs=statement.executeQuery(query);
        JSONArray array=new JSONArray();
        if (rs.next()) {
            JSONObject object=new JSONObject();
            String[] allProfit = rs.getString("profit").split(",");
            String[] allWeight = rs.getString("weight").split(",");
            String cubage=rs.getString("cubage");
            String dimension=rs.getString("dimension");
            int length=Integer.valueOf(dimension)/3;
            int[][] profit=new int[length+1][4];
            int[][] weight=new int[length+1][4];
            for (int i = 0; i < length ; i++) {
                for (int j = 0; j < 3; j++) {
                    profit[i][j]=Integer.valueOf(allProfit[3*i+j]);
                    weight[i][j]=Integer.valueOf(allWeight[3*i+j]);
                }
            }
            BacktrackProgramming back=new BacktrackProgramming();
            back.setValue(profit);
            back.setWeight(weight);
            back.setRow(length);
            object.put("group",group);
            object.put("filename",filename);
            object.put("time",back.backTrack(Integer.valueOf(cubage)));
            object.put("res",back.getRes());
            array.put(object);
        }
        json.put("code",0);
        json.put("msg","");
        json.put("count",array.length());
        json.put("data",array);
        JDBCUtils.release(rs,statement,connection);
        JDBCUtils.recordJournal("回溯法解决最优解","查询操作");
        return json.toString();
    }

    @RequestMapping("/genetic/{filename}/{group}")
    public String genetic(@PathVariable String filename,@PathVariable Integer group) throws SQLException, IOException {
        JSONObject json=new JSONObject();
        Connection connection = JDBCUtils.getConnection();
        Statement statement = connection.createStatement();
        String query="SELECT * FROM `knapsack` WHERE `filename`='"+filename+"' AND `group`='"+group+"'";
        ResultSet rs=statement.executeQuery(query);
        JSONArray array=new JSONArray();
        if (rs.next()) {
            JSONObject object=new JSONObject();
            String[] allProfit = rs.getString("profit").split(",");
            String[] allWeight = rs.getString("weight").split(",");
            String cubage=rs.getString("cubage");
            String dimension=rs.getString("dimension");
            int length=Integer.valueOf(dimension)/3;
            int[][] profit=new int[length+1][4];
            int[][] weight=new int[length+1][4];
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < 3; j++) {
                    profit[i][j]=Integer.valueOf(allProfit[3*i+j]);
                    weight[i][j]=Integer.valueOf(allWeight[3*i+j]);
                }
            }
            GeneticProgramming genetic=new GeneticProgramming(100, length, 3000, 0.8f, 0.9f);
            genetic.setB(weight);
            genetic.setV(profit);
            genetic.setPb(Integer.valueOf(cubage));
            Double time = genetic.doGenetic();
            object.put("group",group);
            object.put("filename",filename);
            object.put("time",time);
            object.put("bestT",genetic.getBestT());
            object.put("bestLength",genetic.getBestLength());
            array.put(object);
        }
        json.put("count",array.length());
        json.put("code",0);
        json.put("msg","");
        json.put("data",array);
        JDBCUtils.release(rs,statement,connection);
        JDBCUtils.recordJournal("遗传算法解决最优解","查询操作");
        return json.toString();
    }

    @RequestMapping("/journalShow")
    public String journalShow() throws SQLException {
        JSONObject json=new JSONObject();
        Connection connection = JDBCUtils.getConnection();
        Statement statement = connection.createStatement();
        String query="SELECT * FROM `journal`";
        ResultSet rs=statement.executeQuery(query);
        JSONArray array=new JSONArray();
        while (rs.next()){
            JSONObject object=new JSONObject();
            object.put("operation",rs.getString("operation"));
            object.put("command",rs.getString("command"));
            object.put("time",rs.getString("time"));
            array.put(object);
        }
        json.put("data",array);
        json.put("count",array.length());
        json.put("code",0);
        json.put("msg","");
        JDBCUtils.release(rs,statement,connection);
        JDBCUtils.recordJournal("打印日志数据","查询操作");
        return json.toString();
    }

    private void writeDataBase(InitAnalysis initFile,File file) throws SQLException {
        ArrayList<ProfitWeight> list = initFile.getList();
        Connection connection = JDBCUtils.getConnection();
        Statement statement = connection.createStatement();
        String query="SELECT * FROM `knapsack` WHERE `filename`='"+file.getName()+"'";
        ResultSet rs=statement.executeQuery(query);
        if (rs.next()){
            return;
        }
        for (int i=0;i< list.size();i++) {
            int dimension=Integer.valueOf(list.get(i).getDimension().substring(2))*3;
            int cubage=Integer.valueOf(list.get(i).getCubage());
            String profit=list.get(i).getProfit();
            String weight=list.get(i).getWeight();
            String insert="insert into `knapsack` (`dimension`, `cubage`, `profit`, `weight`,`filename`,`group`) " +
                    "VALUES ('"+dimension+"','"+cubage+"','"+profit+"','"+weight+"','"+file.getName()+"','"+(i+1)+"')";
            statement.execute(insert);
        }
        JDBCUtils.release(statement,connection);
        JDBCUtils.recordJournal("文件传输并解析文件","插入操作");
    }
}
