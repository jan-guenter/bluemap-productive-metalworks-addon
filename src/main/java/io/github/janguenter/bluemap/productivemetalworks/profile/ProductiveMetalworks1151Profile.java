/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.productivemetalworks.profile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Exact All the Mons 1.2.0 profile `productivemetalworks-1.21.1-1.15.1`. */
public final class ProductiveMetalworks1151Profile {

    public static final String PROFILE_ID = "productivemetalworks-1.21.1-1.15.1";
    public static final String PRODUCTIVE_LIB_PATH =
            "META-INF/jarjar/productivelib-1.21.1-0.2.0.jar";
    public static final long PRODUCTIVE_LIB_SIZE = 154_249L;
    public static final String PRODUCTIVE_LIB_SHA256 =
            "6671c8aa783d5fc3056b5a24b041edcf51b9c774b68fd85a790ae3346e4550e7";
    public static final List<ArtifactPin> ARTIFACTS = List.of(
            new ArtifactPin(
                    "productiveMetalworks",
                    "productivemetalworks",
                    "1.21.1-1.15.1",
                    "productivemetalworks-1.21.1-1.15.1.jar",
                    3_033_210L,
                    "100132424f9659b76fd1326a8f0068a58b91d6d94351d47484b5b9cee394e812"
            )
    );

    private ProductiveMetalworks1151Profile() {
    }

    /** Verifies the embedded library that owns the persisted fluid and energy schema. */
    public static boolean matchesProductiveLib(Path outerJar) {
        if (outerJar == null) {
            return false;
        }
        try (ZipFile zip = new ZipFile(outerJar.toFile())) {
            ZipEntry entry = zip.getEntry(PRODUCTIVE_LIB_PATH);
            if (entry == null || entry.isDirectory() || entry.getSize() != PRODUCTIVE_LIB_SIZE) {
                return false;
            }
            byte[] bytes;
            try (InputStream input = zip.getInputStream(entry)) {
                bytes = input.readNBytes((int) PRODUCTIVE_LIB_SIZE + 1);
            }
            return bytes.length == PRODUCTIVE_LIB_SIZE
                    && PRODUCTIVE_LIB_SHA256.equals(digest(bytes));
        } catch (IOException exception) {
            return false;
        }
    }

    private static String digest(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }
}
