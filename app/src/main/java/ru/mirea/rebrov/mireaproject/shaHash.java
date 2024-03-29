package ru.mirea.rebrov.mireaproject;
import java.nio.charset.StandardCharsets;
import android.util.Log;

public class shaHash {
    private static final int[] K = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    private static int[] initializeHash() {
        return new int[]{
                0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
                0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
        };
    }

    public static String hashSHA256(String input) {
        byte[] message = input.getBytes(StandardCharsets.UTF_8);

        int initialLength = message.length;
        int paddedLength = paddingLength(initialLength);
        byte[] paddedMessage = new byte[paddedLength];
        System.arraycopy(message, 0, paddedMessage, 0, initialLength);

        paddedMessage[initialLength] = (byte) 0x80;

        long bitLength = (long) initialLength * 8;
        for (int i = 0; i < 8; i++) {
            paddedMessage[paddedLength - 8 + i] = (byte) ((bitLength >>> (56 - i * 8)) & 0xFF);
        }

        int numBlocks = paddedLength / 64;
        int[] w = new int[64];

        int[] hash = initializeHash();
        for (int block = 0; block < numBlocks; block++) {
            for (int i = 0; i < 16; i++) {
                w[i] = getIntFromBytes(paddedMessage, block * 64 + i * 4);
            }

            for (int i = 16; i < 64; i++) {
                int s0 = rotateRight(w[i - 15], 7) ^ rotateRight(w[i - 15], 18) ^ (w[i - 15] >>> 3);
                int s1 = rotateRight(w[i - 2], 17) ^ rotateRight(w[i - 2], 19) ^ (w[i - 2] >>> 10);
                w[i] = w[i - 16] + s0 + w[i - 7] + s1;
            }

            int[] tempHash = hash.clone();
            for (int i = 0; i < 64; i++) {
                int s1 = rotateRight(tempHash[4], 6) ^ rotateRight(tempHash[4], 11) ^ rotateRight(tempHash[4], 25);
                int ch = (tempHash[4] & tempHash[5]) ^ (~tempHash[4] & tempHash[6]);
                int temp1 = tempHash[7] + s1 + ch + K[i] + w[i];
                int s0 = rotateRight(tempHash[0], 2) ^ rotateRight(tempHash[0], 13) ^ rotateRight(tempHash[0], 22);
                int maj = (tempHash[0] & tempHash[1]) ^ (tempHash[0] & tempHash[2]) ^ (tempHash[1] & tempHash[2]);
                int temp2 = s0 + maj;

                tempHash[7] = tempHash[6];
                tempHash[6] = tempHash[5];
                tempHash[5] = tempHash[4];
                tempHash[4] = tempHash[3] + temp1;
                tempHash[3] = tempHash[2];
                tempHash[2] = tempHash[1];
                tempHash[1] = tempHash[0];
                tempHash[0] = temp1 + temp2;
            }

            for (int i = 0; i < 8; i++) {
                hash[i] += tempHash[i];
            }
        }

        StringBuilder hashHex = new StringBuilder();
        for (int value : hash) {
            hashHex.append(String.format("%08x", value));
        }

        Log.d("SHA256", hashHex.toString());

        return hashHex.toString();
    }

    private static int paddingLength(int messageLength) {
        int paddingLength = 64 - ((messageLength + 8) % 64);
        if (paddingLength < 8) {
            paddingLength += 64;
        }
        return messageLength + paddingLength + 8;
    }

    private static int getIntFromBytes(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) | ((bytes[offset + 1] & 0xFF) << 16)
                | ((bytes[offset + 2] & 0xFF) << 8) | (bytes[offset + 3] & 0xFF);
    }

    private static int rotateRight(int value, int bits) {
        return (value >>> bits) | (value << (32 - bits));
    }
}
