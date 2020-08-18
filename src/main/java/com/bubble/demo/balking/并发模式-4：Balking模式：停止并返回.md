[toc]
# 并发模式-4：Balking模式：停止并返回

## Balking模式
- 思想：
Balking就是停止返回的意思。
Balking模式：`如果现在不适合或没必要执行这个操作，就停止处理，直接返回`。

- 与Guarded Suspension保护暂停模式区别
Balking模式Guarded Suspension保护暂停模式一样都需要守护条件。
在Balking模式中，如果守护条件不成立，则立即中断处理。而后者是一直等待到可执行。

![Balking模式的Timethreads图](https://cdn.jsdelivr.net/gh/bubblewu/cdn/images/concurrent/balking-timethreads.png)

## 案例
### 场景
例如：`文本的自动保存功能`，防止电脑突然宕机，定期的将数据保存到文件中。

定期将某些数据写入文件中。每次写入都会覆盖上次写入到内容，也就是说只有最新的内容才会被保存。
但需注意：当本次写入数据与上次数据内容完全相同时，就不再执行写入操作，直接返回。

也就是说，该场景下**数据内容存在不同是守护条件。如果守护条件不成立，也就是数据相同，则不再执行写入操作，直接返回（Balk）**。

### 实现
Data类对应文本工具的文本内容，SaverThread类对应执行自动保存的线程，而ChangerThread类是模仿用户操作，即对文本修改并随时保存的用户。

![Balking模式案例](https://cdn.jsdelivr.net/gh/bubblewu/cdn/images/concurrent/balking.png)

- Main：
```java
public static void main(String[] args) {
        Data data = new Data("data.txt", "(empty)");
        new ChangerThread("ChangerThread", data).start();
        new SaverThread("SaverThread", data).start();
    }
```
输出：
依次输出，没有重复的编号。
```
SaverThread save content: No.0
SaverThread save content: No.1
ChangerThread save content: No.2
ChangerThread save content: No.3
SaverThread save content: No.4
SaverThread save content: No.5
ChangerThread save content: No.6
ChangerThread save content: No.7
SaverThread save content: No.8
SaverThread save content: No.9
ChangerThread save content: No.10
ChangerThread save content: No.11
```

- Data类
```java
/**
 * 表示可以修改并保存的数据类
 *
 * @author wugang
 * date: 2020-08-18 11:16
 **/
public class Data {
    /**
     * 保存的文件名称
     */
    private final String fileName;
    /**
     * 数据内容
     */
    private String content;
    /**
     * 守护条件：修改后的内容如果还未保存，就未true
     */
    private boolean changed;

    public Data(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
        this.changed = true;
    }

    /**
     * 修改数据内容
     *
     * @param newContent 新内容
     */
    public synchronized void change(String newContent) {
        content = newContent;
        changed = true;
    }

    /**
     * 若数据内容已经修改，则保存到文件中
     */
    public synchronized void save() {
        if (!changed) {
            return;
        }
        doSave();
        changed = false;
    }

    /**
     * 将数据内容保存到文件
     */
    private void doSave() {
        System.out.println(Thread.currentThread().getName() + " save content: " + content);
        try {
            Writer writer = new FileWriter(fileName);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            System.err.println(String.format("save error, fileName = %s, content = %s", fileName, content) + e);
        }
    }

}
```

- ChangerThread类
```java
/**
 * 修改并保存数据内容的类
 *
 * @author wugang
 * date: 2020-08-18 11:18
 **/
public class ChangerThread extends Thread {
    private final Data data;
    private final Random random;

    public ChangerThread(String name, Data data) {
        super(name);
        this.data = data;
        this.random = new Random();
    }

    @Override
    public void run() {
        for (int i = 0; true ; i++) {
            try {
                data.change("No." + i);
                Thread.sleep(random.nextInt(1000));
                data.save();
            } catch (InterruptedException ignored) {
            }
        }
    }

}
```

- SaverThread类
```java
/**
 * 定期保存数据内容的类
 *
 * @author wugang
 * date: 2020-08-18 11:17
 **/
public class SaverThread extends Thread {
    private final Data data;

    public SaverThread(String name, Data data) {
        super(name);
        this.data = data;
    }

    @Override
    public void run() {
        while (true) {
            // 保存数据
            data.save();
            try {
                // 每隔1s就保存一次
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
```
