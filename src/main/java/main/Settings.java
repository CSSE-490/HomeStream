package main;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public enum Settings {
    SETTINGS;

    public List<File> searchableDirectories = new LinkedList<>();
}
