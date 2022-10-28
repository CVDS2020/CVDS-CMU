package com.css.cvds.cmu.utils;

import com.google.common.collect.*;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chendj
 * @since 2022/10/15
 */
@SuppressWarnings("unused")
public class CollectUtils {

    private CollectUtils() {

    }

    public static <T, R> List<R> toList(Collection<T> collection, Function<T, R> function) {
        if (CollectionUtils.isEmpty(collection)) {
            return Lists.newArrayList();
        }
        return collection.stream().filter(Objects::nonNull).map(function).filter(Objects::nonNull)
                         .collect(Collectors.toList());
    }

    public static <T, R> List<R> toUniqueList(Collection<T> collection, Function<T, R> function) {
        if (CollectionUtils.isEmpty(collection)) {
            return Lists.newArrayList();
        }
        return collection.stream().filter(Objects::nonNull).map(function).filter(Objects::nonNull).distinct()
                         .collect(Collectors.toList());
    }

    public static <T, R> Set<R> toSet(Collection<T> collection, Function<T, R> function) {
        if (CollectionUtils.isEmpty(collection)) {
            return Sets.newHashSet();
        }
        return collection.stream().filter(Objects::nonNull).map(function).filter(Objects::nonNull)
                         .collect(Collectors.toSet());
    }

    public static <T, K> Map<K, T> toMap(Collection<T> collection, Function<T, K> function) {
        return toMap(collection, function, Function.identity());
    }

    public static <T, K, V> Map<K, V> toMap(Collection<T> collection, Function<T, K> keyFunc, Function<T, V> valFunc) {
        if (CollectionUtils.isEmpty(collection)) {
            return Maps.newHashMap();
        }
        return collection.stream().filter(Objects::nonNull).collect(Collectors.toMap(keyFunc, valFunc));
    }

    public static <T, K> Multimap<K, T> toMultimap(Collection<T> collection, Function<T, K> keyFunc) {
        return toMultimap(collection, keyFunc, Function.identity());
    }

    public static <T, K, V> Multimap<K, V> toMultimap(Collection<T> collection, Function<T, K> keyFunc,
                                                      Function<T, V> valFunc) {
        if (CollectionUtils.isEmpty(collection)) {
            return HashMultimap.create();
        }
        Multimap<K, V> multimap = HashMultimap.create();
        collection.stream().filter(Objects::nonNull).forEach(t -> {
            multimap.put(keyFunc.apply(t), valFunc.apply(t));
        });
        return multimap;
    }

    public static <T, K> ListMultimap<K, T> toListMultimap(Collection<T> collection, Function<T, K> keyFunc) {
        return toListMultimap(collection, keyFunc, Function.identity());
    }

    public static <T, K, V> ListMultimap<K, V> toListMultimap(Collection<T> collection, Function<T, K> keyFunc,
                                                              Function<T, V> valFunc) {
        if (CollectionUtils.isEmpty(collection)) {
            return ArrayListMultimap.create();
        }
        ListMultimap<K, V> listMultimap = ArrayListMultimap.create();
        collection.stream().filter(Objects::nonNull).forEach(t -> {
            listMultimap.put(keyFunc.apply(t), valFunc.apply(t));
        });
        return listMultimap;
    }

    public static <T, R, C> Table<R, C, T> toTable(Collection<T> collection, Function<T, R> rowFunc,
                                                   Function<T, C> columnFunc) {
        return toTable(collection, rowFunc, columnFunc, Function.identity());
    }

    public static <T, R, C, V> Table<R, C, V> toTable(Collection<T> collection, Function<T, R> rowFunc,
                                                      Function<T, C> columnFunc, Function<T, V> valFunc) {
        if (CollectionUtils.isEmpty(collection)) {
            return HashBasedTable.create();
        }
        Table<R, C, V> table = HashBasedTable.create();
        collection.stream().filter(Objects::nonNull).forEach(t -> {
            table.put(rowFunc.apply(t), columnFunc.apply(t), valFunc.apply(t));
        });
        return table;
    }
}
