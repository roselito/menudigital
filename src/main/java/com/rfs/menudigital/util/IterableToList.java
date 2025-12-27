package com.rfs.menudigital.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 * @author Roselito@RFS
 */
public class IterableToList<T> {
    public List<T> converter(Iterable<T> i){
        return StreamSupport.stream(i.spliterator(), false).collect(Collectors.toList());
    }
}
