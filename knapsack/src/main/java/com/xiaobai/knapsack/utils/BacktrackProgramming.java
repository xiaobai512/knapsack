package com.xiaobai.knapsack.utils;

//全局变量设置
public class BacktrackProgramming {
    private int weight[][];						//数据集中读入的重量，第一维表示第几组，第二维表示该组的第一个元素
    private int value[][];						//数据集中读入的价格
    private int row=1;
    private int col=3;						//数据集分组背包个数，及每个背包的物品个数
    private int res=0;								//最优解
    private int back_weight;//背包总容量
    private int back_value=0;			//回溯法记录部分最优解的临时变量
    private long back_count=0;						//回溯法执行次数，用来判断回溯法能否在有效时

    public void setRow(int row) {
        this.row = row;
    }

    public int getRes() {
        return res;
    }

    public void setWeight(int[][] weight) {
        this.weight = weight;
    }

    public void setValue(int[][] value) {
        this.value = value;
    }

    public void setBack_weight(int back_weight) {
        this.back_weight = back_weight;
    }

    public Double backTrack(int Weight){
        long start=System.currentTimeMillis();
        back_weight=Weight;
        back_value=0;
        dfs(0);
        long end=System.currentTimeMillis();
        return (end-start)/1000.0;
    }

    //回溯法求解
    private void dfs(int x){
        if(x+1>=row) {
            return ;
        }
        else {
            if(weight[x+1][3]<=back_weight) {
                back_weight-=weight[x+1][3];
                back_value+=value[x+1][3];
                if(res<back_value) {
                    res=back_value;
                }
                dfs(x+1);
                back_weight+=weight[x+1][3];
                back_value-=value[x+1][3];
            }
            dfs(x+1);
            if(weight[x+1][1]<=back_weight) {
                back_weight-=weight[x+1][1];
                back_value+=value[x+1][1];
                dfs(x+1);
                if(res<back_value) {
                    res=back_value;
                }
                back_weight+=weight[x+1][1];
                back_value-=value[x+1][1];
            }
            if(weight[x+1][2]<=back_weight) {
                back_weight-=weight[x+1][2];
                back_value+=value[x+1][2];
                if(res<back_value) {
                    res=back_value;
                }
                dfs(x+1);
                back_weight+=weight[x+1][2];
                back_value-=value[x+1][2];
            }
        }
    }
}
