package com.xiaobai.knapsack.utils;

import com.xiaobai.knapsack.pojo.ProfitWeight;

import java.util.ArrayList;

public class DynamicProgramming {

    private ProfitWeight profitWeight=new ProfitWeight();
    private Integer optimalSolution;
    //折扣背包问题
    public Long discountBackpack()
    {
        String[] weightValue=this.getProfitWeight().getWeight().split(",");
        String[] profitValue=this.getProfitWeight().getProfit().split(",");

        int[] weight=new int[weightValue.length];
        int[] profit=new int[profitValue.length];

        for (int i = 0; i < weightValue.length; i++) {
            weight[i]=Integer.valueOf(weightValue[i]);
            profit[i]=Integer.valueOf(profitValue[i]);
        }

        return knapSack(weight,profit,Integer.valueOf(profitWeight.getCubage()));
    }

    //参数：重量值、价值、重量
    private Long knapSack(int[] weight, int[] profit, int C)
    {
        int n = profit.length/3;//profit一定是3的倍数
        int[][] maxvalue = new int[n + 1][C + 1];//价值矩阵
        long before=System.currentTimeMillis();
        for (int i = 0; i < maxvalue.length; i++) {
            maxvalue[i][0] = 0;
        }
        for (int i = 0; i < maxvalue[0].length; i++) {
            maxvalue[0][i] = 0;
        }
        for (int i = 1; i < maxvalue.length; i++) {//不处理第一行
            for (int j = 1; j <maxvalue[0].length; j++) {//不处理第一列
                //处理每一个项集
                int index=(i-1)*3;//计算当前的索引值，这里以项集为单位进行计算
                ArrayList<Integer> item=new ArrayList<>();
                if (j<weight[index]&&j<weight[index+1]&&j<weight[index+2])
                {
                    maxvalue[i][j]=maxvalue[i-1][j];
                    continue;
                }
                if(j>=weight[index])
                    item.add(Math.max(maxvalue[i-1][j],profit[index]+maxvalue[i-1][j-weight[index]]));
                if(j>=weight[index+1])
                    item.add(Math.max(maxvalue[i-1][j],profit[index+1]+maxvalue[i-1][j-weight[index+1]]));
                if(j>=weight[index+2])
                    item.add(Math.max(maxvalue[i-1][j],profit[index+2]+maxvalue[i-1][j-weight[index+2]]));

                item.sort((Integer o1, Integer o2)->{
                    if (o1>o2) return -1;
                    else if (o1==o2) return 0;
                    else return 1;
                });
                maxvalue[i][j]=item.get(0);
            }
        }
        long after=System.currentTimeMillis();
        this.setOptimalSolution(maxvalue[n][C]);
        return (after-before);
    }

    public ProfitWeight getProfitWeight() {
        return profitWeight;
    }

    public void setProfitWeight(ProfitWeight profitWeight) {
        this.profitWeight = profitWeight;
    }

    public Integer getOptimalSolution() {
        return optimalSolution;
    }

    public void setOptimalSolution(Integer optimalSolution) {
        this.optimalSolution = optimalSolution;
    }
}
