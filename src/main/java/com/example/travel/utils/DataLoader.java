package com.example.travel.utils;

import java.nio.file.Path;
import java.util.List;

public interface DataLoader <T>{
    List<T> load(Path file);
}
