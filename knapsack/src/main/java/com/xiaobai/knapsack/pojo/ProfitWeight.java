package com.xiaobai.knapsack.pojo;

public class ProfitWeight {
    //存储数据
    private String dimension;
    private String cubage;
    private String profit;
    private String weight;

    public ProfitWeight() {
    }

    public ProfitWeight(String dimension, String cubage, String profit, String weight) {
        this.dimension=dimension;
        this.cubage=cubage;
        this.profit = profit;
        this.weight = weight;
    }

    public String getProfit() {
        return profit;
    }

    public String getWeight() {
        return weight;
    }

    public String getDimension() {
        return dimension;
    }

    public String getCubage() {
        return cubage;
    }

    @Override
    public String toString() {
        return "ProfitWeight{" +
                "dimension='" + dimension + '\'' +
                ", cubage='" + cubage + '\'' +
                ", profit='" + profit + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }
}
