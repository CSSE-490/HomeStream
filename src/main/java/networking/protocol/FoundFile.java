package networking.protocol;

import java.util.Arrays;

public class FoundFile {

    public String fileName;
    public byte[] checksum;
    public long length;

    public FoundFile() {}

    public FoundFile(String name, byte[] checksum, long length) {
        this.fileName = name;
        this.checksum = checksum;
        this.length = length;
    }

    @Override
    public String toString() {
        return "FoundFile{" +
                "fileName='" + fileName + '\'' +
                ", checksum=" + Arrays.toString(checksum) +
                '}';
    }
}
