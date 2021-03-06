package anddd7.guava.sample;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Table;
import com.google.common.collect.TreeRangeMap;
import com.google.common.collect.TreeRangeSet;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.testng.annotations.Test;

public class CollectionSample {

  /**
   * 不可变集合 ,add/remove方法都被移除
   */
  public void immutable() {
    final ImmutableSet<String> COLOR_NAMES =
        ImmutableSet.copyOf(
            ImmutableSet.of(
                "red", "orange", "yellow",
                "green", "blue", "purple")
        );

    System.out.println(
        COLOR_NAMES.asList().get(0));
    //COLOR_NAMES.add("white");
  }

  /**
   * 更多集合
   *
   * @Multiset 可添加相同元素 ,并计数类似 Map<String,Integer>
   * @Multimap Map+Set组合使用 Map<String,List>
   * @Bimap 双向绑定的map
   * @Table 具有多个key的map
   */
  @Test
  public void moreCollection() {
    //multiset
    HashMultiset<String> hashMultiset = HashMultiset.create();
    hashMultiset.add("a");
    hashMultiset.add("b");
    hashMultiset.add("a");
    System.out.println(
        hashMultiset.count("a")
    );

    //multimap
    ArrayListMultimap<Integer, String> arrayListMultimap = ArrayListMultimap.create();
    arrayListMultimap.put(1, "Hello");
    arrayListMultimap.put(1, ",");
    arrayListMultimap.put(1, "friends");
    arrayListMultimap.get(1).add("!");

    arrayListMultimap.put(2, "Welcome");
    arrayListMultimap.get(2).add("!");

    arrayListMultimap.get(1).forEach(s -> System.out.print(s));
    arrayListMultimap.get(2).forEach(s -> System.out.print(s));

    //biMap
    BiMap<String, Integer> nameId = HashBiMap.create();
    //C
    nameId.put("A", 1);
    System.out.println(nameId.inverse().get(1));
    //U
    nameId.forcePut("A", 2);
    System.out.println(nameId.inverse().get(2));

    //Table
    Table<Integer, Integer, Integer> table99 = HashBasedTable.create();
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9; j++) {
        table99.put(i + 1, j + 1, (i + 1) * (j + 1));
      }
    }
    Map<Integer, Integer> row6 = table99.row(6);
    Map<Integer, Integer> col3 = table99.column(3);
    System.out.println(row6.get(3) + " is equals " + col3.get(6) + " ? " +
        (row6.get(3) == col3.get(6)));

  }

  /**
   * @ClassToInstanceMap 根据class筛选对象
   * @Range
   */
  @Test
  public void moreCollection2() {
    //存放class : instance
    ClassToInstanceMap<Object> classMap = MutableClassToInstanceMap.create();
    classMap.putInstance(Integer.class, Integer.valueOf(0));
    classMap.putInstance(Person.class, new Person(1, "A"));
    System.out.println(classMap.get(Person.class));

    //Rangeset 描述数学意义上的区间
    RangeSet<Integer> rangeSet = TreeRangeSet.create();
    rangeSet.add(Range.closed(1, 10)); // {[1,10]}
    rangeSet.add(Range.closedOpen(11, 15));//不相连区间:{[1,10], [11,15)}
    rangeSet.add(Range.closedOpen(15, 20)); //相连区间; {[1,10], [11,20)}
    rangeSet.add(Range.openClosed(0, 0)); //空区间; {[1,10], [11,20)}
    rangeSet.remove(Range.open(5, 10)); //分割[1, 10]; {[1,5], [10,10], [11,20)}

    //RangeMap 区间映射
    RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
    rangeMap.put(Range.closed(1, 10), "foo"); //{[1,10] => "foo"}
    rangeMap.put(Range.open(3, 6), "bar"); //{[1,3] => "foo", (3,6) => "bar", [6,10] => "foo"}
    rangeMap.put(Range.open(10, 20),
        "foo"); //{[1,3] => "foo", (3,6) => "bar", [6,10] => "foo", (10,20) => "foo"}
    rangeMap.remove(Range.closed(5, 11)); //{[1,3] => "foo", (3,5) => "bar", (11,20) => "foo"}
  }

  /**
   * 改进的工具类
   */
  @Test
  public void utils() {
    //工厂类使用(避免泛型的重复定义 ,1.7以后使用<>方便很多)
    Lists.newArrayList();
    Maps.newHashMap();

    //Iterables ,用来表示还未完全加载(数据分页)的数据集
    Iterable<Integer> concatenated = Iterables.concat(
        Ints.asList(1, 2, 3),
        Ints.asList(4, 5, 6));
    Iterable<Integer> simple = Iterables.concat(Ints.asList(1));

    System.out.println(Iterables.getLast(concatenated));
    System.out.println(Iterables.getOnlyElement(simple));

    //Maps
    Map<Integer, Person> personMap = Maps.uniqueIndex(
        Lists.newArrayList(
            new Person(1, "A"),
            new Person(2, "B")),
        input -> input.getId());

    //...

  }

  @Test
  public void withJava8() {
    Supplier<Stream<Integer>> streamSupplier = () -> IntStream.range(1, 100)
        .mapToObj(value -> value);

    //collect list
    print(
        streamSupplier.get().collect(() -> new ArrayList(), (list, value) -> list.add(value),
            (list1, list2) -> list1.addAll(list2)));
    print(
        streamSupplier.get().collect(ArrayList::new, List::add, List::addAll));
    print(
        streamSupplier.get().collect(Collectors.toList()));
    //collect map
    print(
        streamSupplier.get().collect(Maps::newHashMap, (hashMap, obj) -> hashMap.put(obj % 2, obj),
            (hashMap, hashMap2) -> hashMap.putAll(hashMap2)));
    print(
        streamSupplier.get().collect(Collectors.toMap(p -> p % 2, p -> p, (o, o2) -> o2)));
    //collect map<i,list>
    print(
        streamSupplier.get().collect(Collectors.groupingBy(o -> o % 2)));

    //multimap
    Multimap multimap = ArrayListMultimap.create();
    streamSupplier.get().forEach(integer -> multimap.put(integer % 2, integer));
    print(multimap);

  }

  public void print(Object target) {
    System.out.println(target);
  }
}
