package networking.protocol;

import java.util.Arrays;

public class FoundFile {

    public String fileName;
    public byte[] checksum;

    public FoundFile() {}

    public FoundFile(String name, byte[] checksum) {
        this.fileName = name;
        this.checksum = checksum;
    }

    @Override
    public String toString() {
        return "FoundFile{" +
                "fileName='" + fileName + '\'' +
                ", checksum=" + Arrays.toString(checksum) +
                '}';
    }
}
