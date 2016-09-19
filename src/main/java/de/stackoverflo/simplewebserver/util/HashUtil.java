package de.stackoverflo.simplewebserver.util;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;

public class HashUtil {

    public static synchronized String calculateMD5Hash(File file) throws Exception {
        try {
            byte[] b = Files.readAllBytes(file.toPath());
            byte[] hash = MessageDigest.getInstance("MD5").digest(b);

            return DatatypeConverter.printHexBinary(hash).toLowerCase();
        } catch (Exception e) {
            throw new Exception("hash could not be calculated");
        }
    }

}
