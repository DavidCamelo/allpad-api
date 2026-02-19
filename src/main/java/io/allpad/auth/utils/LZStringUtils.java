package io.allpad.auth.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class LZStringUtils {

    static String keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    public static String compress(String uncompressed) {

        if (uncompressed == null)
            return "";
        int value;
        HashMap<String, Integer> contextDictionary = new HashMap<>();
        HashSet<String> contextDictionaryToCreate = new HashSet<>();
        String contextC;
        String contextWC;
        String contextW = "";
        double contextEnlargeIn = 2d; // Compensate for the first entry which
        // should not count
        int contextDictSize = 3;
        int contextNumBits = 2;
        StringBuilder contextDataString = new StringBuilder();
        int contextDataVal = 0;
        int contextDataPosition = 0;

        for (int ii = 0; ii < uncompressed.length(); ii += 1) {
            contextC = "" + (uncompressed.charAt(ii));
            if (!contextDictionary.containsKey(contextC)) {
                contextDictionary.put(contextC, contextDictSize++);
                contextDictionaryToCreate.add(contextC);
            }

            contextWC = contextW + contextC;

            if (contextDictionary.containsKey(contextWC)) {
                contextW = contextWC;
            } else {
                if (contextDictionaryToCreate.contains(contextW)) {

                    if (((int) contextW.charAt(0)) < 256) {
                        for (int i = 0; i < contextNumBits; i++) {
                            contextDataVal = (contextDataVal << 1);
                            if (contextDataPosition == 15) {
                                contextDataPosition = 0;
                                contextDataString.append((char) contextDataVal);
                                contextDataVal = 0;
                            } else {
                                contextDataPosition++;
                            }
                        }
                        value = contextW.charAt(0);
                        for (int i = 0; i < 8; i++) {
                            contextDataVal = (contextDataVal << 1)
                                    | (value & 1);
                            if (contextDataPosition == 15) {
                                contextDataPosition = 0;
                                contextDataString.append((char) contextDataVal);
                                contextDataVal = 0;
                            } else {
                                contextDataPosition++;
                            }
                            value = value >> 1;
                        }
                    } else {
                        value = 1;
                        for (int i = 0; i < contextNumBits; i++) {
                            contextDataVal = (contextDataVal << 1) | value;
                            if (contextDataPosition == 15) {
                                contextDataPosition = 0;
                                contextDataString.append((char) contextDataVal);
                                contextDataVal = 0;
                            } else {
                                contextDataPosition++;
                            }
                            value = 0;
                        }
                        value = contextW.charAt(0);
                        for (int i = 0; i < 16; i++) {
                            contextDataVal = (contextDataVal << 1)
                                    | (value & 1);
                            if (contextDataPosition == 15) {
                                contextDataPosition = 0;
                                contextDataString.append((char) contextDataVal);
                                contextDataVal = 0;
                            } else {
                                contextDataPosition++;
                            }
                            value = value >> 1;
                        }
                    }
                    contextEnlargeIn--;
                    if (Double.valueOf(contextEnlargeIn).intValue() == 0) {
                        contextEnlargeIn = Math.pow(2, contextNumBits);
                        contextNumBits++;
                    }
                    contextDictionaryToCreate.remove(contextW);
                } else {
                    value = contextDictionary.get(contextW);
                    for (int i = 0; i < contextNumBits; i++) {
                        contextDataVal = (contextDataVal << 1)
                                | (value & 1);
                        if (contextDataPosition == 15) {
                            contextDataPosition = 0;
                            contextDataString.append((char) contextDataVal);
                            contextDataVal = 0;
                        } else {
                            contextDataPosition++;
                        }
                        value = value >> 1;
                    }

                }
                contextEnlargeIn--;
                if (Double.valueOf(contextEnlargeIn).intValue() == 0) {
                    contextEnlargeIn = Math.pow(2, contextNumBits);
                    contextNumBits++;
                }
                // Add wc to the dictionary.
                contextDictionary.put(contextWC, contextDictSize++);
                contextW = contextC;
            }
        }

        // Output the code for w.
        if (!contextW.isEmpty()) {
            if (contextDictionaryToCreate.contains(contextW)) {
                if (((int) contextW.charAt(0)) < 256) {
                    for (int i = 0; i < contextNumBits; i++) {
                        contextDataVal = (contextDataVal << 1);
                        if (contextDataPosition == 15) {
                            contextDataPosition = 0;
                            contextDataString.append((char) contextDataVal);
                            contextDataVal = 0;
                        } else {
                            contextDataPosition++;
                        }
                    }
                    value = contextW.charAt(0);
                    for (int i = 0; i < 8; i++) {
                        contextDataVal = (contextDataVal << 1)
                                | (value & 1);
                        if (contextDataPosition == 15) {
                            contextDataPosition = 0;
                            contextDataString.append((char) contextDataVal);
                            contextDataVal = 0;
                        } else {
                            contextDataPosition++;
                        }
                        value = value >> 1;
                    }
                } else {
                    value = 1;
                    for (int i = 0; i < contextNumBits; i++) {
                        contextDataVal = (contextDataVal << 1) | value;
                        if (contextDataPosition == 15) {
                            contextDataPosition = 0;
                            contextDataString.append((char) contextDataVal);
                            contextDataVal = 0;
                        } else {
                            contextDataPosition++;
                        }
                        value = 0;
                    }
                    value = contextW.charAt(0);
                    for (int i = 0; i < 16; i++) {
                        contextDataVal = (contextDataVal << 1)
                                | (value & 1);
                        if (contextDataPosition == 15) {
                            contextDataPosition = 0;
                            contextDataString.append((char) contextDataVal);
                            contextDataVal = 0;
                        } else {
                            contextDataPosition++;
                        }
                        value = value >> 1;
                    }
                }
                contextEnlargeIn--;
                if (Double.valueOf(contextEnlargeIn).intValue() == 0) {
                    contextEnlargeIn = Math.pow(2, contextNumBits);
                    contextNumBits++;
                }
                contextDictionaryToCreate.remove(contextW);
            } else {
                value = contextDictionary.get(contextW);
                for (int i = 0; i < contextNumBits; i++) {
                    contextDataVal = (contextDataVal << 1) | (value & 1);
                    if (contextDataPosition == 15) {
                        contextDataPosition = 0;
                        contextDataString.append((char) contextDataVal);
                        contextDataVal = 0;
                    } else {
                        contextDataPosition++;
                    }
                    value = value >> 1;
                }

            }
            contextEnlargeIn--;
            if (Double.valueOf(contextEnlargeIn).intValue() == 0) {
                contextNumBits++;
            }
        }

        // Mark the end of the stream
        value = 2;
        for (int i = 0; i < contextNumBits; i++) {
            contextDataVal = (contextDataVal << 1) | (value & 1);
            if (contextDataPosition == 15) {
                contextDataPosition = 0;
                contextDataString.append((char) contextDataVal);
                contextDataVal = 0;
            } else {
                contextDataPosition++;
            }
            value = value >> 1;
        }

        // Flush the last char
        while (true) {
            contextDataVal = (contextDataVal << 1);
            if (contextDataPosition == 15) {
                contextDataString.append((char) contextDataVal);
                break;
            } else
                contextDataPosition++;
        }
        return contextDataString.toString();
    }

    public static String decompressHexString(String hexString) {

        if (hexString == null) {
            return "";
        }

        if (hexString.length() % 2 != 0) {
            throw new RuntimeException("Input string length should be divisible by two");
        }

        int[] intArr = new int[hexString.length() / 2];

        for (int i = 0, k = 0; i < hexString.length(); i += 2, k++) {
            intArr[k] = Integer.parseInt("" + hexString.charAt(i) + hexString.charAt(i + 1), 16);
        }

        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < intArr.length; j += 2) {
            sb.append(Character.toChars(intArr[j] | intArr[j + 1] << 8));
        }

        return decompress(sb.toString());
    }

    public static String decompress(String compressed) {

        if (compressed == null)
            return "";
        if (compressed.isEmpty())
            return null;
        List<String> dictionary = new ArrayList<>(200);
        double enlargeIn = 4;
        int dictSize = 4;
        int numBits = 3;
        String entry;
        StringBuilder result;
        String w;
        int bits;
        int resB;
        double maxPower;
        int power;
        String c = "";
        int d;
        LZData lzData = LZData.builder()
                .string(compressed)
                .val(compressed.charAt(0))
                .position(32768)
                .index(1)
                .build();

        for (int i = 0; i < 3; i += 1) {
            dictionary.add(i, "");
        }

        bits = 0;
        maxPower = Math.pow(2, 2);
        power = 1;
        while (power != Double.valueOf(maxPower).intValue()) {
            resB = lzData.val & lzData.position;
            lzData.position >>= 1;
            if (lzData.position == 0) {
                lzData.position = 32768;
                lzData.val = lzData.string.charAt(lzData.index++);
            }
            bits |= (resB > 0 ? 1 : 0) * power;
            power <<= 1;
        }

        switch (bits) {
            case 0:
                maxPower = Math.pow(2, 8);
                power = 1;
                while (power != Double.valueOf(maxPower).intValue()) {
                    resB = lzData.val & lzData.position;
                    lzData.position >>= 1;
                    if (lzData.position == 0) {
                        lzData.position = 32768;
                        lzData.val = lzData.string.charAt(lzData.index++);
                    }
                    bits |= (resB > 0 ? 1 : 0) * power;
                    power <<= 1;
                }
                c += (char) bits;
                break;
            case 1:
                bits = 0;
                maxPower = Math.pow(2, 16);
                power = 1;
                while (power != Double.valueOf(maxPower).intValue()) {
                    resB = lzData.val & lzData.position;
                    lzData.position >>= 1;
                    if (lzData.position == 0) {
                        lzData.position = 32768;
                        lzData.val = lzData.string.charAt(lzData.index++);
                    }
                    bits |= (resB > 0 ? 1 : 0) * power;
                    power <<= 1;
                }
                c += (char) bits;
                break;
            case 2:
                return "";

        }

        dictionary.add(3, c);
        w = c;
        result = new StringBuilder(200);
        result.append(c);

        // w = result = c;

        while (true) {
            if (lzData.index > lzData.string.length()) {
                return "";
            }

            bits = 0;
            maxPower = Math.pow(2, numBits);
            power = 1;
            while (power != Double.valueOf(maxPower).intValue()) {
                resB = lzData.val & lzData.position;
                lzData.position >>= 1;
                if (lzData.position == 0) {
                    lzData.position = 32768;
                    lzData.val = lzData.string.charAt(lzData.index++);
                }
                bits |= (resB > 0 ? 1 : 0) * power;
                power <<= 1;
            }

            String temp = "";

            switch (d = bits) {
                case 0:
                    maxPower = Math.pow(2, 8);
                    power = 1;
                    while (power != Double.valueOf(maxPower).intValue()) {
                        resB = lzData.val & lzData.position;
                        lzData.position >>= 1;
                        if (lzData.position == 0) {
                            lzData.position = 32768;
                            lzData.val = lzData.string.charAt(lzData.index++);
                        }
                        bits |= (resB > 0 ? 1 : 0) * power;
                        power <<= 1;
                    }

                    temp += (char) bits;
                    dictionary.add(dictSize++, temp);

                    d = dictSize - 1;

                    enlargeIn--;

                    break;
                case 1:
                    bits = 0;
                    maxPower = Math.pow(2, 16);
                    power = 1;
                    while (power != Double.valueOf(maxPower).intValue()) {
                        resB = lzData.val & lzData.position;
                        lzData.position >>= 1;
                        if (lzData.position == 0) {
                            lzData.position = 32768;
                            lzData.val = lzData.string.charAt(lzData.index++);
                        }
                        bits |= (resB > 0 ? 1 : 0) * power;
                        power <<= 1;
                    }

                    temp = "";
                    temp += (char) bits;

                    dictionary.add(dictSize++, temp);

                    d = dictSize - 1;

                    enlargeIn--;

                    break;
                case 2:
                    return result.toString();
            }

            if (Double.valueOf(enlargeIn).intValue() == 0) {
                enlargeIn = Math.pow(2, numBits);
                numBits++;
            }

            if (d < dictionary.size() && dictionary.get(d) != null) {
                entry = dictionary.get(d);
            } else {
                if (d == dictSize) {
                    entry = w + w.charAt(0);
                } else {
                    return null;
                }
            }

            result.append(entry);

            // Add w+entry[0] to the dictionary.
            dictionary.add(dictSize++, w + entry.charAt(0));
            enlargeIn--;

            w = entry;

            if (Double.valueOf(enlargeIn).intValue() == 0) {
                enlargeIn = Math.pow(2, numBits);
                numBits++;
            }

        }
    }

    public static String compressToUTF16(String input) {
        if (input == null)
            return "";
        StringBuilder output = new StringBuilder();
        int c;
        int current = 0;
        int status = 0;

        input = LZStringUtils.compress(input);

        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            switch (status++) {
                case 0:
                    output.append((char) ((c >> 1) + 32));
                    current = (c & 1) << 14;
                    break;
                case 1:
                    output.append((char) ((current + (c >> 2)) + 32));
                    current = (c & 3) << 13;
                    break;
                case 2:
                    output.append((char) ((current + (c >> 3)) + 32));
                    current = (c & 7) << 12;
                    break;
                case 3:
                    output.append((char) ((current + (c >> 4)) + 32));
                    current = (c & 15) << 11;
                    break;
                case 4:
                    output.append((char) ((current + (c >> 5)) + 32));
                    current = (c & 31) << 10;
                    break;
                case 5:
                    output.append((char) ((current + (c >> 6)) + 32));
                    current = (c & 63) << 9;
                    break;
                case 6:
                    output.append((char) ((current + (c >> 7)) + 32));
                    current = (c & 127) << 8;
                    break;
                case 7:
                    output.append((char) ((current + (c >> 8)) + 32));
                    current = (c & 255) << 7;
                    break;
                case 8:
                    output.append((char) ((current + (c >> 9)) + 32));
                    current = (c & 511) << 6;
                    break;
                case 9:
                    output.append((char) ((current + (c >> 10)) + 32));
                    current = (c & 1023) << 5;
                    break;
                case 10:
                    output.append((char) ((current + (c >> 11)) + 32));
                    current = (c & 2047) << 4;
                    break;
                case 11:
                    output.append((char) ((current + (c >> 12)) + 32));
                    current = (c & 4095) << 3;
                    break;
                case 12:
                    output.append((char) ((current + (c >> 13)) + 32));
                    current = (c & 8191) << 2;
                    break;
                case 13:
                    output.append((char) ((current + (c >> 14)) + 32));
                    current = (c & 16383) << 1;
                    break;
                case 14:
                    output.append((char) ((current + (c >> 15)) + 32));
                    output.append((char) ((c & 32767) + 32));

                    status = 0;
                    break;
            }
        }

        output.append((char) (current + 32));

        return output.toString();
    }

    public static String decompressFromUTF16(String input) {
        if (input == null)
            return "";
        StringBuilder output = new StringBuilder(200);
        int current = 0, c, status = 0, i = 0;

        while (i < input.length()) {
            c = (((int) input.charAt(i)) - 32);

            switch (status++) {
                case 0:
                    current = c << 1;
                    break;
                case 1:
                    output.append((char) (current | (c >> 14)));
                    current = (c & 16383) << 2;
                    break;
                case 2:
                    output.append((char) (current | (c >> 13)));
                    current = (c & 8191) << 3;
                    break;
                case 3:
                    output.append((char) (current | (c >> 12)));
                    current = (c & 4095) << 4;
                    break;
                case 4:
                    output.append((char) (current | (c >> 11)));
                    current = (c & 2047) << 5;
                    break;
                case 5:
                    output.append((char) (current | (c >> 10)));
                    current = (c & 1023) << 6;
                    break;
                case 6:
                    output.append((char) (current | (c >> 9)));
                    current = (c & 511) << 7;
                    break;
                case 7:
                    output.append((char) (current | (c >> 8)));
                    current = (c & 255) << 8;
                    break;
                case 8:
                    output.append((char) (current | (c >> 7)));
                    current = (c & 127) << 9;
                    break;
                case 9:
                    output.append((char) (current | (c >> 6)));
                    current = (c & 63) << 10;
                    break;
                case 10:
                    output.append((char) (current | (c >> 5)));
                    current = (c & 31) << 11;
                    break;
                case 11:
                    output.append((char) (current | (c >> 4)));
                    current = (c & 15) << 12;
                    break;
                case 12:
                    output.append((char) (current | (c >> 3)));
                    current = (c & 7) << 13;
                    break;
                case 13:
                    output.append((char) (current | (c >> 2)));
                    current = (c & 3) << 14;
                    break;
                case 14:
                    output.append((char) (current | (c >> 1)));
                    current = (c & 1) << 15;
                    break;
                case 15:
                    output.append((char) (current | c));

                    status = 0;
                    break;
            }

            i++;
        }

        return LZStringUtils.decompress(output.toString());
        // return output;

    }

    public static String decompressFromBase64(String input) {
        return LZStringUtils.decompress(decode64(input));
    }

    // implemented from JS version
    public static String decode64(String input) {

        StringBuilder str = new StringBuilder(200);

        int ol = 0;
        int output = 0;
        int chr1, chr2, chr3;
        int enc1, enc2, enc3, enc4;
        int i = 0;

        while (i < input.length()) {

            enc1 = keyStr.indexOf(input.charAt(i++));
            enc2 = keyStr.indexOf(input.charAt(i++));
            enc3 = keyStr.indexOf(input.charAt(i++));
            enc4 = keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            if (ol % 2 == 0) {
                output = chr1 << 8;

                if (enc3 != 64) {
                    str.append((char) (output | chr2));
                }
                if (enc4 != 64) {
                    output = chr3 << 8;
                }
            } else {
                str.append((char) (output | chr1));

                if (enc3 != 64) {
                    output = chr2 << 8;
                }
                if (enc4 != 64) {
                    str.append((char) (output | chr3));
                }
            }
            ol += 3;
        }

        return str.toString();
    }

    public static String encode64(String input) {
        StringBuilder result = new StringBuilder((input.length() * 8 + 1) / 3);
        for (int i = 0, max = input.length() << 1; i < max;) {
            int left = max - i;
            if (left >= 3) {
                int ch1 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
                i++;
                int ch2 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
                i++;
                int ch3 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
                i++;
                result.append(keyStr.charAt((ch1 >> 2) & 0x3f));
                result.append(keyStr.charAt(((ch1 << 4) + (ch2 >> 4)) & 0x3f));
                result.append(keyStr.charAt(((ch2 << 2) + (ch3 >> 6)) & 0x3f));
                result.append(keyStr.charAt(ch3 & 0x3f));
            } else if (left == 2) {
                int ch1 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
                i++;
                int ch2 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
                i++;
                result.append(keyStr.charAt((ch1 >> 2) & 0x3f));
                result.append(keyStr.charAt(((ch1 << 4) + (ch2 >> 4)) & 0x3f));
                result.append(keyStr.charAt(((ch2 << 2)) & 0x3f));
                result.append('=');
            } else if (left == 1) {
                int ch1 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
                i++;
                result.append(keyStr.charAt((ch1 >> 2) & 0x3f));
                result.append(keyStr.charAt(((ch1 << 4)) & 0x3f));
                result.append('=');
                result.append('=');
            }
        }
        return result.toString();
    }

    public static String compressToBase64(String input) {
        return encode64(compress(input));
    }

}

