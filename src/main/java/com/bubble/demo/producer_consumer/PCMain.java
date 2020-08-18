package com.bubble.demo.producer_consumer;

/**
 * 生产者消费者模式Demo：
 * 场景：
 * 旋转小餐厅里，有3位师傅制作蛋糕放到桌子上，然后有3位客人来吃这些蛋糕。
 * 主要业务点：
 * - 师傅（MakerThread）制作蛋糕（String），并将其放置在桌子（Table）上；
 * - 桌子上最多可以放置3个蛋糕；
 * - 如果桌子上已经放满3个，就需等有空余位置时才能继续放置；
 * - 客人（EaterThread）按蛋糕放置等顺序来取桌子（Table）上等蛋糕来吃；
 * - 当桌子没有蛋糕时，客人就需等待直到有蛋糕放入；
 *
 * @author wugang
 * date: 2020-07-16 19:54
 **/
public class PCMain {

    public static void main(String[] args) {
        Table table = new Table(3);
        for (int i = 0; i < 3; i++) {
            MakerThread makerThread = new MakerThread("-> Maker." + i, table, 2020 + i);
            makerThread.start();
            EaterThread eaterThread = new EaterThread("Eater." + i, table, 2020 + i);
            eaterThread.start();
        }
    }

//    private static void testTableQueue() {
//        TableQueue table = new TableQueue(3);
//        for (int i = 0; i < 3; i++) {
//            MakerThread makerThread = new MakerThread("-> Maker." + i, table, 2020 + i);
//            makerThread.start();
//            EaterThread eaterThread = new EaterThread("Eater." + i, table, 2020 + i);
//            eaterThread.start();
//        }
//    }

}
