package io.github.shanepark;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Phone phone = new Phone("iPhone15", "010-1234-5678", "0000");

        Serializer<Phone> phoneSerializer = new Serializer<>();
        byte[] serialize = phoneSerializer.serialize(phone);
        System.out.println("Serialized byte array:" + Arrays.toString(serialize));

        lookupSerialVersionUid();

        byte[] serialized = new byte[]{-84, -19, 0, 5, 115, 114, 0, 25, 105, 111, 46, 103, 105, 116, 104, 117, 98, 46, 115, 104, 97, 110, 101, 112, 97, 114, 107, 46, 80, 104, 111, 110, 101, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 3, 76, 0, 5, 109, 111, 100, 101, 108, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 76, 0, 6, 110, 117, 109, 98, 101, 114, 113, 0, 126, 0, 1, 76, 0, 8, 112, 97, 115, 115, 119, 111, 114, 100, 113, 0, 126, 0, 1, 120, 112, 116, 0, 8, 105, 80, 104, 111, 110, 101, 49, 53, 116, 0, 13, 48, 49, 48, 45, 49, 50, 51, 52, 45, 53, 54, 55, 56, 116, 0, 4, 48, 48, 48, 48};
        Phone deserializePhone = phoneSerializer.deserialize(serialized);
        System.out.println("\ndeserializePhone = " + deserializePhone);

        String json = "{\"model\":\"iPhone15\",\"number\":\"010-1234-5678\",\"password\":\"0000\"}";
        System.out.println("sizeSerialized = " + (long) serialized.length);
        System.out.println("sizeJson = " + (long) json.getBytes().length);

        performanceTest();
    }

    private static void lookupSerialVersionUid() {
        ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(Phone.class);
        long serialVersionUID = objectStreamClass.getSerialVersionUID();
        System.out.println("\nserialVersionUID = " + serialVersionUID);
    }

    private static void performanceTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Serializer<Phone> phoneSerializer = new Serializer<>();

        System.out.println();
        System.out.println("test json vs java");

        int testCount = 1_000_000;
        long start;

        start = System.currentTimeMillis();
        for (int i = 0; i < testCount; i++) {
            testSpeedJava(new Phone("iPhone" + i, String.valueOf(i), ""), phoneSerializer);
        }
        System.out.println("Test Java took " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < testCount; i++) {
            testSpeedJson(new Phone("iPhone" + i, String.valueOf(i), ""), mapper);
        }
        System.out.println("Test Json took " + (System.currentTimeMillis() - start) + "ms");
    }

    private static void testSpeedJson(Phone phone, ObjectMapper mapper) throws IOException {
        String json = mapper.writeValueAsString(phone);
        Phone recovered = mapper.readValue(json, Phone.class);
        assert recovered != null;
    }

    private static void testSpeedJava(Phone phone, Serializer<Phone> phoneSerializer) {
        try {
            byte[] serialize = phoneSerializer.serialize(phone);
            Phone recovered = phoneSerializer.deserialize(serialize);
            assert recovered != null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static class Serializer<T> {
        private final Base64.Encoder base64Encoder = Base64.getEncoder();

        public byte[] serialize(T object) throws IOException {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
            ) {
                objectOutputStream.writeObject(object);
                return byteArrayOutputStream.toByteArray();
            }
        }

        public String serializeBase64(T object) throws IOException {
            byte[] serialized = serialize(object);
            return base64Encoder.encodeToString(serialized);
        }

        public T deserialize(byte[] serialized) throws IOException, ClassNotFoundException {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serialized);
                 ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
            ) {
                return (T) objectInputStream.readObject();
            }
        }
    }

}
