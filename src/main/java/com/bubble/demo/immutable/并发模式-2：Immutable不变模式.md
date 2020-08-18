[toc]

# 并发模式-2：Immutable不变模式

Immutable不变模式就是指：**确保实例的内部状态不会发生改变，这样在访问这些实例时就不需要增加耗时的互斥处理**（如Single Threaded Execution模式中的对临界区进行互斥保护）。

如`String类`就是一个Immutable类。因为**String类中使用final关键字修饰字符串数组`private final char value[];`来保存字符串**，并没有修改字符串内容的方法。所以，String的实例所表示的字符串的内容不会发生变化。

## Immutable不变模式
### 是什么？
Immutable角色是一个类，在这个角色中，**字段的值是不可以修改的，也不存在修改字段内容的方法。** `Immutable角色的实例被创建后，状态就不会再发生变化，也就不需要使用Single Threaded Execution模式使用synchronized去保护临界区`。

### 何时使用？
Immutable模式该在哪些情况下使用呢？
- `实例创建后，状态不再发生变化时`：
**实例创建后，状态不再发生变化**是必要条件。实例的状态是由字段的值决定的，所以**将字段声明为`final字段`，且`不存在setter方法`是重点所在**。
但即使这样，也有可能是可变的，因为**即使字段的值不发生变化，但字段引用的实例有可能会发生变化**。

- `实例是共享的，且被频繁访问时`：
Immutable模式的优点是**不使用synchronized来保护临界区**。就意味着**能够在不失去安全性和生存性的前提下提高性能**。所以在当实例被多个线程共享时，且有可能被频繁访问时，Immutable模式的优点就会极大的凸显出来。

### 成对的mutable可变类和immutable不可变类
假设一个类，被多线程访问，使用synchronized进行保护，但类中存在setter方法。这样看起来Immutable模式是不成立的。
- 场景一：
如果这个setter方法并未被使用，就可以将字段声明为final并删除setter方法，这样就遵守了不可变性，就成功改造为Immutable模式了。

- 场景二：
如果setter方法被使用了，这个类就是mutable可变模式了。
我们可以分析该类，如可以分为使用setter方法和不使用的情况，就可以将这个类拆分为mutable类和immutable类，然后设计成可以根据mutable实例创建immutable实例，也可以反过来根据immutable实例创建mutable实例。
如：StringBuffer类和String类。
StringBuffer类是mutable类，表示的字符能够随便改写，使用了synchronized保护。而String类表示字符串不可以被改写，也没使用synchronized保护，所以性能比较高。
但**StringBuffer类中有一个以String为参数的构造函数，而String类中有一个以StringBuffer为参数的构造函数**。也就是，`两者的实例是可以互相转换的`。
```java
// String类的构造函数
  public String(StringBuffer buffer) {
        synchronized(buffer) {
            this.value = Arrays.copyOf(buffer.getValue(), buffer.length());
        }
    }
//StringBuffer类的构造函数
    public StringBuffer(String str) {
        super(str.length() + 16);
        append(str);
    }
```
所以，`如果需要频繁改变字符串内容，就使用StringBuffer类，如果不需要改变，只是引用其内容，就使用String类`。
但`当多个字符串组成新的字符串时，StringBuffer类的速度比String类快`。

注意：
**在Immutable类中调用mutable类时需注意安全性，需要对mutable类进行安全保护，否则，可变类中的值可能会被其他线程使用该类的setter方法改写字段值，导致值发生变化**。
如：
```java
public String(StringBuffer buffer) {
        synchronized(buffer) {
            this.value = Arrays.copyOf(buffer.getValue(), buffer.length());
        }
    }
```

### 标准类库中的Immutable模式
- 表示字符串的`java.lang.String类`：
再创建完实例后，字符串的内容不会发生变化，因为**使用`final关键字`修饰字符串数组`private final char value[];`来保存字符串**，并没有修改字符串内容的方法。

- 表示大数字的`java.math.BigInteger类`和`java.math.BigDecimal类`：
- 表示正则表达式模式的`java.util.regex.Pattern类`：
Pattern类表示正则表达式的模式，即使在处理模式匹配时，值也不会发生变化。

- `java.lang.Integer类等`：
Integer和Short等`基本类型的包装类（wrapper class）`都是immutable类型的，创建好实例后，也都不会发生变化。


## 案例
创建一个Person类，并启动三个线程来访问该实例，会发现它们都是线程安全的。

- Person类
```java

/**
 * 表示人的类：
 * - 线程安全的：
 * 字段值仅可以通过构造函数来设置，没有setXX方法。
 * 所以，Person类的实例一旦创建，其字段的值就不会发生变化。
 * 这时，即使多个线程同时访问同一个实例，该类也是安全的。
 * Person类中的所有方法无需声明为synchronized，就可以允许多个线程同时执行。
 *
 * - 防止子类修改其字段值：
 * 1、Person声明为final类型。表示我们无法创建其类的子类，也是防止子类修改其字段值的一种措施。
 * 2、字段的可见性都为private。表示这2个字段都只有从该类的内部才可以访问。
 * 3、字段都声明为final类型。表示一旦字段被赋值一次，就不会再被赋值。
 *
 * @author wugang
 * date: 2020-07-31 18:55
 **/
public final class Person {
    private final String name;
    private final String address;

    public Person(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "[Person: " + "name = " + name + ", address = " + address + ']';
    }
}
```

- PrintPersonThread类：
```java
/**
 * 显示Person实例的线程的类
 *
 * @author wugang
 * date: 2020-07-31 18:55
 **/
public class PrintPersonThread extends Thread {
    private Person person;

    public PrintPersonThread(Person person) {
        this.person = person;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // sleep 让各线程可以清晰的交叉打印
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " prints " + person.toString());
        }
    }

}
```

- Main类：
```java
/**
     * 创建一个Person类，并启动三个线程来访问该实例
     */
    public static void main(String[] args) {
        Person person = new Person("Bubble", "北京");
        new PrintPersonThread(person).start();
        new PrintPersonThread(person).start();
        new PrintPersonThread(person).start();
    }
```

输出：
```
Thread-1 prints [Person: name = Bubble, address = 北京]
Thread-2 prints [Person: name = Bubble, address = 北京]
Thread-0 prints [Person: name = Bubble, address = 北京]
Thread-2 prints [Person: name = Bubble, address = 北京]
Thread-0 prints [Person: name = Bubble, address = 北京]
```

## 扩展
### 相关的设计模式
#### Single Threaded Execution模式
Immutable模式下，实例的状态不会发生变化，所以无需进行保护。
而STE模式，当一个线程正在修改实例状态时，不允许其他的线程来访问该实例。
这时会出现下面两种情况之一：
- `写入与写入的冲突`（write-write conflict）：
当一个线程正在修改实例状态，而其他线程也试图修改其状态时发生的冲突。
- `读取和写入的冲突`（read-write conflict）：
当一个线程正在读取实例状态，而其他线程试图修改其状态时发生的冲突。

而immutable模式中，只会发生read-read当情况，不会出现conflict。

#### Read-Wrire Lock模式
在Read-Write Lock模式中，`读取操作和写入操作是分开考虑的。在执行读取操作之前，线程必须获取用于读取的锁；在执行写入操作之前，线程必须获取用于写入的锁`。所以：
- **当一个线程在读取时，其他线程可以读取，但是不可以写入**。
- **当一个线程正在写入时，其他线程不可以读取或写入**。
因为执行互斥处理会降低程序的性能，但是如果把写入的互斥处理和读取的互斥处理分开来考虑，就可以提高系统性能。

Immutable模式中，只会发生read-read当情况，不会出现conflict。所以多线程可以自由的访问实例。
而Read-Write Lock模式也利用了read-read不会引起冲突的特点。它执行read的线程和执行write的线程是分开考虑的。能够提高程序的性能。

#### Flyweight模式（享元模式）
享元模式的主要目的是`实现对象的共享`，即共享池，当系统中对象多的时候可以减少内存的开销，通常与工厂模式一起使用。
在Flyweight模式中，为了提高内存的使用效率，会共享实例。所以，Immutable模式和Flyweight模式有时是可以同时使用的。

### final关键字
final类主要用在三个地方：类、方法、变量。

Java中的final类有多种不同的用途，含义也不同。
- `final类`：
当final修饰一个类时，表示该类不能被继承，即无法扩展。也就是说无法创建final类的子类，所以final类中声明的方法也就不会被重写。
final类中的所有成员方法都会被隐式地指定为final方法；

- `final方法`：
实例方法使用final，表示该方法不会被子类的方法重写。即可以把方法锁定，以防止任何继承类修改它的含义。
静态方法使用final，表示该方法不会被子类的方法隐藏，如果试图重写或隐藏编译时会提示错误。
类中所有的private方法都被隐式地指定为final。

- `final变量`：
对于一个final变量，如果是基本数据类型的变量，则其数值一旦初始化之后就不能更改；
如是引用类型的变量，则对其初始化之后便不能再让它指向另一个对象。
> - **final字段**：
> final字段只能被赋值一次。
> 对`final实例字段赋值`的方法有2种：
> 1、一种在字段声明时赋上初始值；
> 2、一种在构造函数中对字段赋值；
> 对`final静态字段赋值`的方法也有2种：
> 1、一种在字段声明时赋上初始值；
> 2、在static静态代码块中对字段赋值；
> 注意：final字段不可以使用setter方法再次赋值。

> - **final变量和final参数**：
> 局部变量和方法的参数，也可以声明为final，可以赋值一次。
> 但final参数不可以赋值，因为调用方法时，已经对其赋值了。


### 集合类和多线程
#### 非线程安全的ArrayList类
java.util.ArrayList类用于提供可调整大小的数组，是非线程安全的。


#### Collections.synchronizedList同步集合类
java.util.ArrayList类是非线程安全的类，可以使用Collections.synchronizedList方法对其进行同步，就能得到线程安全的实例。
```java
final List<String> list = Collections.synchronizedList(new ArrayList<>());
```

#### 写时复制（copy-on-write）的CopyOnWriteArrayList类
java.util.concurrent.CopyOnWriteArrayList类是线程安全的。与使用Collections.synchronizedList不同，它**采用了`写时复制Copy-On-Write技术`来避免读写冲突**。
如果使用Copy-On-Write，当对集合执行写操作时，内部已确保安全的数组就会被整体复制。复制之后，就不需在使用迭代器依次读取元素时担心元素会被修改了。
```java
    public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            newElements[len] = e;
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }
```

但在使用copy-on-write时，每次执行写操作时，都会执行复制，会耗费较多时间。所以该类适合在`写少读多，且读操作频率非常高`的场景。

