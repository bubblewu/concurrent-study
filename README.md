# concurrent-study
Java并发相关的多线程案例

## demo： 多线程设计模式案例
### Single Threaded Execution模式，即"以一个线程执行"。
就像独木桥一样，同一时间内只允许一个人通过，**该模式用于设置限制，以确保同一时间内只能让一个线程执行处理。**
- 其实`主要思想`也就是：
<p> 当我们修改多个线程共享的实例时，实例就会失去安全性。所以我们找出这个不安全的范围，将这个范围设置为临界区，并对临界区进行保护（使用synchronized），使其只允许一个线程同时执行。

### Immutable不变模式
<p> Immutable不变模式就是指：**确保实例的内部状态不会发生改变，这样在访问这些实例时就不需要增加耗时的互斥处理**（如Single Threaded Execution模式中的对临界区进行互斥保护）。
<p> 如`String类`就是一个Immutable类。因为**String类中使用final关键字修饰字符串数组`private final char value[];`来保存字符串**，并没有修改字符串内容的方法。所以，String的实例所表示的字符串的内容不会发生变化。
