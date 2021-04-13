package com.xiaobai.knapsack.utils;

import java.io.IOException;
import java.util.Random;

public class GeneticProgramming {
    private int[][] v ;// 物品价值
    private int[][] b ;// 物品体积
    private int pb;// 背包容积

    private int LL; // 染色体长度
    private int scale;// 种群规模
    private int MAX_GEN; // 运行代数

    private int bestT;// 最佳出现代数
    private int bestLength; // 最佳编码价值
    private int[] bestTour; // 最佳编码
    private int[] best_str;

    // 初始种群，父代种群，行数表示种群规模，一行代表一个个体，即染色体，列表示染色体基因片段
    private int[][] oldPopulation;
    private int[][] newPopulation;// 新的种群，子代种群
    private int[] fitness;// 种群适应度，表示种群中各个个体的适应度
    private int[][] fitness_str;

    private float[] Pi;// 种群中各个个体的累计概率
    private float Pc;// 交叉概率
    private float Pm;// 变异概率
    private int t;// 当前代数
    private Random random;

    // 种群规模，染色体长度,最大代数，交叉率，变异率
    public GeneticProgramming(int s, int l, int g, float c, float m) {
        scale = s;
        LL = l;
        MAX_GEN = g;
        Pc = c;
        Pm = m;
    }

    public int getBestLength() {
        return bestLength;
    }

    public int getBestT() {
        return bestT;
    }

    public void setV(int[][] v) {
        this.v = v;
    }

    public void setB(int[][] b) {
        this.b = b;
    }

    public void setPb(int pb) {
        this.pb = pb;
    }

    public class best_one {
        private int x;
        private int y[];
    }

    private void init() throws IOException {
        bestLength = 0;
        bestTour = new int[LL];
        best_str = new int[LL];
        bestT = 0;
        t = 0;

        newPopulation = new int[scale][LL];
        oldPopulation = new int[scale][LL];
        fitness = new int[scale];
        fitness_str = new int[scale][LL];
        Pi = new float[scale];

        random = new Random(System.currentTimeMillis());
    }

    // 初始化种群
    private void initGroup() {
        int k, i;
        for (k = 0; k < scale; k++)// 种群数
        {
            // 01编码
            for (i = 0; i < LL; i++) {
                oldPopulation[k][i] = random.nextInt(65535) % 2;
            }
        }
    }

    private best_one evaluate(int[] chromosome) {
        // 010110
        int vv = 0;
        int bb = 0;
        int str[]=new int[LL];
        // 染色体，起始城市,城市1,城市2...城市n
        for (int i = 0; i < LL; i++) {
            if (chromosome[i] == 1) {
                int temp=random.nextInt(65535) % 3;
                vv+=v[i][temp];
                bb+=b[i][temp];
                str[i]=temp+1;
            }
            else {
                str[i]=0;
            }
        }
        if (bb > pb) {
            // 超出背包体积
            best_one x =new best_one();
            x.x=0;x.y=str;
            return x;
        } else {
            best_one x =new best_one();
            x.x=vv;x.y=str;
            return x;
        }
    }

    // 计算种群中各个个体的累积概率，前提是已经计算出各个个体的适应度fitness[max]，作为赌轮选择策略一部分，Pi[max]
    private void countRate() {
        int k;
        double sumFitness = 0;// 适应度总和

        int[] tempf = new int[scale];

        for (k = 0; k < scale; k++) {
            tempf[k] = fitness[k];
            sumFitness += tempf[k];
        }

        Pi[0] = (float) (tempf[0] / sumFitness);
        for (k = 1; k < scale; k++) {
            Pi[k] = (float) (tempf[k] / sumFitness + Pi[k - 1]);
        }
    }

    // 挑选某代种群中适应度最高的个体，直接复制到子代中
    // 前提是已经计算出各个个体的适应度Fitness[max]
    private void selectBestGh() {
        int k, i, maxid;
        int maxevaluation;
        int max_str[] = null;

        maxid = 0;
        maxevaluation = fitness[0];
        for (k = 1; k < scale; k++) {
            if (maxevaluation < fitness[k]) {
                maxevaluation = fitness[k];
                max_str=fitness_str[k];
                maxid = k;
            }
        }

        if (bestLength < maxevaluation) {
            bestLength = maxevaluation;
            best_str=max_str;
            bestT = t;// 最好的染色体出现的代数;
            for (i = 0; i < LL; i++) {
                bestTour[i] = oldPopulation[maxid][i];
            }
        }

        // 复制染色体，k表示新染色体在种群中的位置，kk表示旧的染色体在种群中的位置
        copyGh(0, maxid);// 将当代种群中适应度最高的染色体k复制到新种群中，排在第一位0
    }

    // 复制染色体，k表示新染色体在种群中的位置，kk表示旧的染色体在种群中的位置
    private void copyGh(int k, int kk) {
        int i;
        for (i = 0; i < LL; i++) {
            newPopulation[k][i] = oldPopulation[kk][i];
        }
    }

    // 赌轮选择策略挑选
    private void select() {
        int k, i, selectId;
        float ran1;
        for (k = 1; k < scale; k++) {
            ran1 = (float) (random.nextInt(65535) % 1000 / 1000.0);
            // System.out.println("概率"+ran1);
            // 产生方式
            for (i = 0; i < scale; i++) {
                if (ran1 <= Pi[i]) {
                    break;
                }
            }
            selectId = i;
            copyGh(k, selectId);
        }
    }

    private void evolution() {
        int k;
        // 挑选某代种群中适应度最高的个体
        selectBestGh();
        // 赌轮选择策略挑选scale-1个下一代个体
        select();
        float r;

        // 交叉方法
        for (k = 0; k < scale; k = k + 2) {
            r = random.nextFloat();// /产生概率
            // System.out.println("交叉率..." + r);
            if (r < Pc) {
                // System.out.println(k + "与" + k + 1 + "进行交叉...");
                OXCross(k, k + 1);// 进行交叉
            } else {
                r = random.nextFloat();// /产生概率
                // System.out.println("变异率1..." + r);
                // 变异
                if (r < Pm) {
                    // System.out.println(k + "变异...");
                    OnCVariation(k);
                }
                r = random.nextFloat();// /产生概率
                // System.out.println("变异率2..." + r);
                // 变异
                if (r < Pm) {
                    // System.out.println(k + 1 + "变异...");
                    OnCVariation(k + 1);
                }
            }

        }

    }


    // 两点交叉算子
    private void OXCross(int k1, int k2) {
        int i, j, flag;
        int ran1, ran2, temp = 0;

        ran1 = random.nextInt(65535) % LL;
        ran2 = random.nextInt(65535) % LL;

        while (ran1 == ran2) {
            ran2 = random.nextInt(65535) % LL;
        }
        if (ran1 > ran2)// 确保ran1<ran2
        {
            temp = ran1;
            ran1 = ran2;
            ran2 = temp;
        }
        flag = ran2 - ran1 + 1;// 个数
        for (i = 0, j = ran1; i < flag; i++, j++) {
            temp = newPopulation[k1][j];
            newPopulation[k1][j] = newPopulation[k2][j];
            newPopulation[k2][j] = temp;
        }

    }

    // 多次对换变异算子
    private void OnCVariation(int k) {
        int ran1, ran2, temp;
        int count;// 对换次数
        count = random.nextInt(65535) % LL;

        for (int i = 0; i < count; i++) {

            ran1 = random.nextInt(65535) % LL;
            ran2 = random.nextInt(65535) % LL;
            while (ran1 == ran2) {
                ran2 = random.nextInt(65535) % LL;
            }
            temp = newPopulation[k][ran1];
            newPopulation[k][ran1] = newPopulation[k][ran2];
            newPopulation[k][ran2] = temp;
        }
    }

    private void solve() {
        int i;
        int k;

        // 初始化种群
        initGroup();
        // 计算初始化种群适应度，Fitness[max]
        for (k = 0; k < scale; k++) {
            best_one temp= evaluate(oldPopulation[k]);
            fitness[k]=temp.x;
            fitness_str[k]=temp.y;
        }

        // 计算初始化种群中各个个体的累积概率，Pi[max]
        countRate();

        for (t = 0; t < MAX_GEN; t++) {
            evolution();
            // 将新种群newGroup复制到旧种群oldGroup中，准备下一代进化
            for (k = 0; k < scale; k++) {
                for (i = 0; i < LL; i++) {
                    oldPopulation[k][i] = newPopulation[k][i];
                }
            }
            // 计算种群适应度
            for (k = 0; k < scale; k++) {
                best_one temp= evaluate(oldPopulation[k]);
                fitness[k]=temp.x;
                fitness_str[k]=temp.y;
            }
            // 计算种群中各个个体的累积概率
            countRate();
        }
    }

    public Double doGenetic() throws IOException {
        long start=System.currentTimeMillis();
        init();
        solve();
        long end=System.currentTimeMillis();
        return (end-start)/1000.0;
    }
}
