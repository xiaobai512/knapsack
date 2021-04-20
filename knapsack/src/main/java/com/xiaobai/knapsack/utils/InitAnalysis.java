package com.xiaobai.knapsack.utils;

import com.xiaobai.knapsack.pojo.ProfitWeight;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


//初始化解析文件
public class InitAnalysis {

    private ArrayList<ProfitWeight> list=new ArrayList<>();

    public ArrayList<ProfitWeight> getList() {
        return list;
    }

    public boolean getAllParameterByFile(String filePath) throws IOException {
        //若目录不存在，则创建目录
        File file=new File(filePath);
        String data= FileUtils.readFileToString(file,"UTF-8");
        //初步格式化件
        String getTextByDeleteWhiteSpace=StringUtils.deleteWhitespace(data);//删除所有的空格
        if (getTextByDeleteWhiteSpace.charAt(2)=='*') {
            int getFirstDKPIndex = getTextByDeleteWhiteSpace.indexOf("DKP");//获得首次出现DKP的位置
            String formatPrefixText = getTextByDeleteWhiteSpace.substring(getFirstDKPIndex + 3);//删除冒号以前的字符
            int getLastFullIndex = formatPrefixText.lastIndexOf('.');//获得最后一次出现.的位置
            int getLastCommaIndex = formatPrefixText.lastIndexOf(',');//获得最后一次出现,的位置
            int index = getLastFullIndex > getLastCommaIndex ? getLastFullIndex : getLastCommaIndex;//获得格式化后的文本
            String formatSuffixText = formatPrefixText.substring(0, index);
            getProfitAndWeight(formatSuffixText);//获取profit和weight
            return true;
        }else{
            return false;
        }
    }
    //获取文件的Profit、Weight
    private void getProfitAndWeight(String text) {
        String[] getSplitByDKP = text.split("DKP");//初步通过DKP剪切，获得各个模块
        for (String modular : getSplitByDKP)
        {
            String[] getSplitByColon=modular.split(":");//二次剪切，每个模块划分为四个子模块，第一个子模块不使用

            //处理第二个小模块
            String[] getDimensionAndCubage=getSplitByColon[1].split(",");//通过逗号切割
            String dimension=getNumberByString(getDimensionAndCubage[0]);
            String cubage=getNumberByString(getDimensionAndCubage[1]);

            //处理第三个小模块
            int getLastCommaIndex1=getSplitByColon[2].lastIndexOf(',');//获得逗号的位置
            int getLastFullIndex1=getSplitByColon[2].lastIndexOf('.');
            int maxIndex1=getLastCommaIndex1>getLastFullIndex1?getLastCommaIndex1:getLastFullIndex1;
            String profit=getSplitByColon[2].substring(0,maxIndex1);

            //处理第四个小模块
            int getLastCommaIndex2=getSplitByColon[3].lastIndexOf(',');
            int getLastFullIndex2=getSplitByColon[3].lastIndexOf('.');
            int maxIndex2=getLastCommaIndex2>getLastFullIndex2?getLastCommaIndex2:getLastFullIndex2;
            String weight=getSplitByColon[3].substring(0,maxIndex2);

            //数据存入链表
            list.add(new ProfitWeight(dimension,cubage,profit,weight));
        }
    }
    private String getNumberByString(String str)
    {
        StringBuffer temp=new StringBuffer();
        for (int i = 0; i < str.length(); i++)
        {
            if ((str.charAt(i)>='0'&&str.charAt(i)<='9')||str.charAt(i)=='*')
            {
                temp.append(str.charAt(i));
            }
        }
        return temp.toString();
    }
}
